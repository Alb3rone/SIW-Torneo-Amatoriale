package it.siw.tornei.controller;

import it.siw.tornei.dto.GiocatoreDTO;
import it.siw.tornei.model.Giocatore;
import it.siw.tornei.service.GiocatoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoint REST consumati dal modulo React della lista giocatori paginata.
 *
 * URL: GET /rest/giocatori?page=0&size=15
 *
 * Perche' un @RestController separato invece di aggiungere metodi al
 * GiocatoreController esistente?
 *   - @RestController = @Controller + @ResponseBody: ogni return viene
 *     serializzato in JSON (via Jackson), non cerca un template.
 *   - Mescolare i due creerebbe confusione: alcuni metodi ritornano nomi di
 *     template ("giocatori/lista"), altri ritornerebbero dati JSON.
 *   - Separandoli, l'URL "/giocatori" e' per l'utente (HTML) e "/rest/giocatori"
 *     e' per il codice JavaScript (JSON).
 *
 * Pattern paginazione:
 *   - Il client manda page e size in query string.
 *   - Il server costruisce un Pageable con l'ordinamento (fisso: cognome ASC).
 *   - Il repository esegue "LIMIT size OFFSET page*size" al DB.
 *   - Ritorniamo una Map "piatta" con content + metadati per la navigazione
 *     ("bottoni 1 2 3"): il React ha tutto cio' che gli serve.
 */
@RestController
@RequestMapping("/rest/giocatori")
public class GiocatoreRestController {

    @Autowired
    private GiocatoreService giocatoreService;

    /**
     * GET /rest/giocatori?page=0&size=15
     *
     * Parametri opzionali con default (page=0, size=15): se l'utente
     * arriva su /rest/giocatori senza query string, prendiamo la prima pagina.
     *
     * Ritorno JSON:
     * {
     *   "content":       [ {id,nome,cognome,ruolo,altezza,nomeSquadra}, ... ],
     *   "page":          0,              // pagina corrente (0-based)
     *   "size":          15,             // dimensione pagina
     *   "totalPages":    4,              // pagine totali (per disegnare 1 2 3 4)
     *   "totalElements": 53              // giocatori totali nel DB
     * }
     */
    @GetMapping
    public Map<String, Object> lista(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        // Difesa: se un utente maligno passa size=1000000, ci proteggiamo.
        if (size < 1)   size = 15;
        if (size > 100) size = 100;
        if (page < 0)   page = 0;

        // Ordinamento fisso per cognome ascendente (deciso dal prof/utente).
        // Nota: PageRequest e' l'implementazione piu' comune di Pageable.
        PageRequest req = PageRequest.of(page, size, Sort.by("cognome").ascending());

        Page<Giocatore> pageResult = giocatoreService.findAllPaged(req);

        // Converto le entita' in DTO uno per uno (Entity -> DTO).
        List<GiocatoreDTO> content = pageResult.getContent()
                .stream()
                .map(GiocatoreDTO::from)
                .toList();

        // Map ordinata: mantengo un JSON prevedibile per il frontend.
        return Map.of(
                "content",       content,
                "page",          pageResult.getNumber(),
                "size",          pageResult.getSize(),
                "totalPages",    pageResult.getTotalPages(),
                "totalElements", pageResult.getTotalElements()
        );
    }
}
