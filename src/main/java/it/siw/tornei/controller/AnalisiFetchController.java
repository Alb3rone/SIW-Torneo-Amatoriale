package it.siw.tornei.controller;

import it.siw.tornei.model.Squadra;
import it.siw.tornei.repository.SquadraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Analisi sperimentale (Sezione 8.2 del PDF di progetto).
 *
 * Caso d'uso: caricare TUTTE le squadre e per ciascuna la lista dei giocatori.
 *
 * Confronta due strategie di accesso ai dati:
 *   - LAZY  (default JPA per @OneToMany): 1 query per le squadre + N query
 *           (una per ogni collezione giocatori acceduta) -> problema N+1.
 *   - JOIN FETCH: una sola query che carica squadre e giocatori insieme.
 *
 * Ritorna una pagina HTML (Thymeleaf) con i tempi misurati in due riquadri
 * + una sezione di conclusioni.
 *
 * @Transactional(readOnly = true) sull'endpoint serve per due motivi:
 *   1) mantenere aperta la sessione Hibernate durante l'accesso alle
 *      collezioni LAZY (altrimenti LazyInitializationException).
 *   2) evitare di scrivere sul DB per errore.
 */
@Controller
@RequestMapping("/admin/analisi-fetch")
public class AnalisiFetchController {

    @Autowired private SquadraRepository squadraRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public String analisi(Model model) {

        // ============ STRATEGIA 1: LAZY ============
        // findAll() e' quello ereditato da JpaRepository: NON fa JOIN FETCH.
        // Quando dopo iteriamo giocatori, Hibernate emette N query aggiuntive.
        long t0 = System.nanoTime();
        List<Squadra> lazyList = squadraRepository.findAll();
        int totaleGiocatoriLazy = 0;
        for (Squadra s : lazyList) {
            // Ogni accesso a s.getGiocatori() sulla singola squadra scatena
            // una query SELECT * FROM giocatori WHERE squadra_id = ? (una per riga).
            // e' proprio la definizione di problema N+1.
            totaleGiocatoriLazy += s.getGiocatori().size();
        }
        long t1 = System.nanoTime();
        double lazyMs = (t1 - t0) / 1_000_000.0;

        // ============ STRATEGIA 2: JOIN FETCH ============
        // Una singola query SQL che porta indietro squadre + giocatori insieme.
        // Nessuna query aggiuntiva quando accedo a s.getGiocatori().
        long t2 = System.nanoTime();
        List<Squadra> joinList = squadraRepository.findAllWithGiocatori();
        int totaleGiocatoriJoin = 0;
        for (Squadra s : joinList) {
            totaleGiocatoriJoin += s.getGiocatori().size();  // gia' in memoria, zero query
        }
        long t3 = System.nanoTime();
        double joinMs = (t3 - t2) / 1_000_000.0;

        // ============ RIEPILOGO ============
        int numSquadre = lazyList.size();

        // I tempi sono sensibili al caching di Hibernate: alla seconda esecuzione
        // LAZY potrebbe apparire piu' veloce perche' i giocatori sono gia' nel
        // 1st-level cache della sessione. Per un confronto piu' onesto in un
        // esperimento reale si ricarica il contesto tra le due strategie.

        model.addAttribute("numSquadre", numSquadre);
        model.addAttribute("totaleGiocatoriLazy", totaleGiocatoriLazy);
        model.addAttribute("totaleGiocatoriJoin", totaleGiocatoriJoin);
        model.addAttribute("lazyMs", String.format("%.2f", lazyMs));
        model.addAttribute("joinMs", String.format("%.2f", joinMs));
        // Numero di query stimate: 1 (le squadre) + N (una per squadra).
        model.addAttribute("lazyQueryStimate", 1 + numSquadre);
        model.addAttribute("joinQueryStimate", 1);

        return "admin/analisi-fetch";
    }
}
