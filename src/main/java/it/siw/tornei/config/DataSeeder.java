package it.siw.tornei.config;

import it.siw.tornei.model.*;
import it.siw.tornei.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Popola il database con dati di esempio realistici al primo avvio.
 * E' idempotente: controlla per nome se l'entita' esiste prima di aggiungerla.
 * In questo modo puoi riavviare il server senza creare duplicati.
 *
 * Per ri-popolare da zero, cancella il contenuto dei database in pgAdmin:
 *   TRUNCATE TABLE partite, commenti, torneo_squadre, giocatori, squadre,
 *                  tornei, arbitri RESTART IDENTITY CASCADE;
 */
@Component
@Order(2)  // dopo DataBootstrap che crea gli utenti
public class DataSeeder implements CommandLineRunner {

    @Autowired private TorneoRepository torneoRepository;
    @Autowired private SquadraRepository squadraRepository;
    @Autowired private GiocatoreRepository giocatoreRepository;
    @Autowired private ArbitroRepository arbitroRepository;
    @Autowired private PartitaRepository partitaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (squadraRepository.count() >= 8) {
            System.out.println(">> [Seed] Dati di esempio gia' presenti, salto.");
            return;
        }

        System.out.println(">> [Seed] Avvio popolamento database con dati di esempio...");

        // ==================== SQUADRE ====================
        // I loghi sono URL pubblici di Wikimedia Commons.
        Squadra juve  = creaSquadra("Juventus",   "Torino",   1897, "Bianconero",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Juventus_FC_2017_logo.svg/256px-Juventus_FC_2017_logo.svg.png");
        Squadra inter = creaSquadra("Inter",      "Milano",   1908, "Nerazzurro",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/FC_Internazionale_Milano_2021.svg/256px-FC_Internazionale_Milano_2021.svg.png");
        Squadra milan = creaSquadra("Milan",      "Milano",   1899, "Rossonero",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Logo_of_AC_Milan.svg/256px-Logo_of_AC_Milan.svg.png");
        Squadra roma  = creaSquadra("Roma",       "Roma",     1927, "Giallorosso",
                "https://upload.wikimedia.org/wikipedia/en/thumb/f/f7/AS_Roma_logo_%282017%29.svg/256px-AS_Roma_logo_%282017%29.svg.png");
        Squadra lazio = creaSquadra("Lazio",      "Roma",     1900, "Biancoceleste",
                "https://upload.wikimedia.org/wikipedia/en/thumb/c/ce/S.S._Lazio_badge.svg/256px-S.S._Lazio_badge.svg.png");
        Squadra napoli = creaSquadra("Napoli",    "Napoli",   1926, "Azzurro",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/SSC_Neapel.svg/256px-SSC_Neapel.svg.png");
        Squadra atalanta = creaSquadra("Atalanta","Bergamo",  1907, "Nerazzurro",
                "https://upload.wikimedia.org/wikipedia/en/thumb/6/66/AtalantaBC.svg/256px-AtalantaBC.svg.png");
        Squadra fiore = creaSquadra("Fiorentina", "Firenze",  1926, "Viola",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Stemma_AC_Firenze.svg/256px-Stemma_AC_Firenze.svg.png");
        Squadra bologna = creaSquadra("Bologna",  "Bologna",  1909, "Rossoblu",
                "https://upload.wikimedia.org/wikipedia/en/thumb/e/e7/Bologna_F.C._1909_logo.svg/256px-Bologna_F.C._1909_logo.svg.png");
        Squadra torino = creaSquadra("Torino",    "Torino",   1906, "Granata",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Torino_FC_Logo.svg/256px-Torino_FC_Logo.svg.png");

        List<Squadra> tutte = List.of(juve, inter, milan, roma, lazio, napoli, atalanta, fiore, bologna, torino);
        System.out.println(">> [Seed] Create " + tutte.size() + " squadre.");

        // ==================== GIOCATORI ====================
        // Lista di 50 nomi+cognomi italiani fittizi ma realistici, TUTTI UNICI.
        // Vengono assegnati 5 a ciascuna delle 10 squadre nell'ordine.
        String[][] nomiGiocatori = {
            // Juventus (5)
            {"Marco",      "Verdi"},      {"Luca",      "Bianchi"},     {"Andrea",    "Rossi"},
            {"Matteo",     "Galli"},      {"Giuseppe",  "Conti"},
            // Inter (5)
            {"Francesco",  "Romano"},     {"Davide",    "Esposito"},    {"Alessandro","Marini"},
            {"Stefano",    "Colombo"},    {"Federico",  "Bruno"},
            // Milan (5)
            {"Mattia",     "Costa"},      {"Riccardo",  "Fontana"},     {"Lorenzo",   "Greco"},
            {"Simone",     "Sartori"},    {"Roberto",   "Marino"},
            // Roma (5)
            {"Giovanni",   "Caruso"},     {"Antonio",   "Ferrari"},     {"Daniele",   "Russo"},
            {"Filippo",    "Ricci"},      {"Gabriele",  "Lombardi"},
            // Lazio (5)
            {"Tommaso",    "Moretti"},    {"Edoardo",   "Barbieri"},    {"Pietro",    "Vitale"},
            {"Cristian",   "Pellegrini"}, {"Mirko",     "Serra"},
            // Napoli (5)
            {"Nicola",     "Mancini"},    {"Alberto",   "Longo"},       {"Vincenzo",  "Donato"},
            {"Massimo",    "Battaglia"},  {"Salvatore", "Marchesi"},
            // Atalanta (5)
            {"Emanuele",   "De Luca"},    {"Leonardo",  "Gentile"},     {"Samuele",   "Riva"},
            {"Christian",  "Gatti"},      {"Manuel",    "Quaranta"},
            // Fiorentina (5)
            {"Diego",      "Ferrara"},    {"Gianluca",  "Palumbo"},     {"Tiziano",   "Testa"},
            {"Michele",    "Coppola"},    {"Paolo",     "Amato"},
            // Bologna (5)
            {"Enrico",     "Sala"},       {"Maurizio",  "Villa"},       {"Aldo",      "Rinaldi"},
            {"Vittorio",   "Caputo"},     {"Bruno",     "Orlando"},
            // Torino (5)
            {"Carlo",      "Silvestri"},  {"Sergio",    "Mazza"},       {"Walter",    "Bellini"},
            {"Vito",       "Genovese"},   {"Luigi",     "Bertolini"}
        };

        Giocatore.Ruolo[] ruoli = Giocatore.Ruolo.values();
        Random rnd = new Random(42);  // seed fisso = stessi dati ogni volta

        int idxGiocatore = 0;
        for (Squadra s : tutte) {
            // 5 giocatori per squadra (1 portiere + 4 di movimento)
            for (int i = 0; i < 5; i++) {
                Giocatore g = new Giocatore();
                g.setNome(nomiGiocatori[idxGiocatore][0]);
                g.setCognome(nomiGiocatori[idxGiocatore][1]);
                g.setDataNascita(LocalDate.of(1990 + rnd.nextInt(14), 1 + rnd.nextInt(12), 1 + rnd.nextInt(27)));
                g.setRuolo(i == 0 ? Giocatore.Ruolo.PORTIERE : ruoli[1 + rnd.nextInt(3)]);
                g.setAltezza(170 + rnd.nextInt(25));
                g.setSquadra(s);
                giocatoreRepository.save(g);
                idxGiocatore++;
            }
        }
        System.out.println(">> [Seed] Creati " + idxGiocatore + " giocatori (nomi univoci).");

        // ==================== ARBITRI ====================
        Arbitro orsato   = creaArbitro("Daniele",  "Orsato",   "ORS-001");
        Arbitro rocchi   = creaArbitro("Gianluca", "Rocchi",   "ROC-002");
        Arbitro mariani  = creaArbitro("Maurizio", "Mariani",  "MAR-003");
        Arbitro guida    = creaArbitro("Marco",    "Guida",    "GUI-004");
        Arbitro doveri   = creaArbitro("Daniele",  "Doveri",   "DOV-005");
        List<Arbitro> arbitri = List.of(orsato, rocchi, mariani, guida, doveri);
        System.out.println(">> [Seed] Creati " + arbitri.size() + " arbitri.");

        // ==================== TORNEI ====================
        Torneo coppaPrimavera = new Torneo();
        coppaPrimavera.setNome("Coppa Italia Amatoriale");
        coppaPrimavera.setAnno(2026);
        coppaPrimavera.setDescrizione("Il torneo amatoriale di primavera che raccoglie le squadre piu' " +
                "blasonate del campionato. Partite di alto livello, classifica avvincente e tanta passione " +
                "per il calcio. Si gioca al sabato pomeriggio sui campi del centro sportivo cittadino.");
        coppaPrimavera.setSquadre(new HashSet<>(List.of(juve, inter, milan, roma, lazio, napoli)));
        torneoRepository.save(coppaPrimavera);

        Torneo torneoEstivo = new Torneo();
        torneoEstivo.setNome("Torneo Estivo Champions");
        torneoEstivo.setAnno(2026);
        torneoEstivo.setDescrizione("Manifestazione estiva di calcio amatoriale. Otto squadre si sfidano " +
                "in gironi all'italiana per conquistare il trofeo. Partite serali nei mesi di giugno e luglio.");
        torneoEstivo.setSquadre(new HashSet<>(List.of(atalanta, fiore, bologna, torino, juve, inter, milan, roma)));
        torneoRepository.save(torneoEstivo);

        System.out.println(">> [Seed] Creati 2 tornei.");

        // ==================== PARTITE ====================
        // 10 partite nella Coppa Italia + 8 partite nel Torneo Estivo
        // Alcune PLAYED con risultato, altre SCHEDULED
        creaPartita(coppaPrimavera, juve,  inter, orsato,  "Stadio Comunale Roma", -30, 2, 1, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, milan, roma,  rocchi,  "Stadio Comunale Roma", -28, 1, 1, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, lazio, napoli, mariani, "Stadio Olimpico Sud", -25, 0, 3, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, juve,  milan, guida,   "Stadio Comunale Roma", -21, 1, 0, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, inter, roma,  doveri,  "Centro Sportivo Nord", -18, 2, 2, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, napoli, juve, orsato,  "Stadio Comunale Roma", -14, 3, 2, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, lazio, inter, rocchi,  "Stadio Olimpico Nord", -10, 1, 1, Partita.Stato.PLAYED);
        creaPartita(coppaPrimavera, milan, napoli, mariani, "Centro Sportivo Nord", -7, 0, 0, Partita.Stato.PLAYED);
        // partite future
        creaPartita(coppaPrimavera, roma,  lazio, guida,   "Stadio Olimpico Sud",  +3, null, null, Partita.Stato.SCHEDULED);
        creaPartita(coppaPrimavera, juve,  roma,  doveri,  "Stadio Comunale Roma", +7, null, null, Partita.Stato.SCHEDULED);

        creaPartita(torneoEstivo, atalanta, fiore,   orsato,  "Campo Estate 1",  -20, 3, 0, Partita.Stato.PLAYED);
        creaPartita(torneoEstivo, bologna,  torino,  rocchi,  "Campo Estate 2",  -17, 2, 2, Partita.Stato.PLAYED);
        creaPartita(torneoEstivo, juve,     atalanta, mariani, "Campo Estate 1", -12, 1, 1, Partita.Stato.PLAYED);
        creaPartita(torneoEstivo, inter,    bologna, guida,   "Campo Estate 2",  -8,  4, 1, Partita.Stato.PLAYED);
        creaPartita(torneoEstivo, milan,    fiore,   doveri,  "Campo Estate 1",  -5,  2, 1, Partita.Stato.PLAYED);
        // future
        creaPartita(torneoEstivo, roma,     torino,  orsato,  "Campo Estate 2",  +5,  null, null, Partita.Stato.SCHEDULED);
        creaPartita(torneoEstivo, atalanta, bologna, rocchi,  "Campo Estate 1",  +10, null, null, Partita.Stato.SCHEDULED);
        creaPartita(torneoEstivo, fiore,    torino,  mariani, "Campo Estate 2",  +14, null, null, Partita.Stato.SCHEDULED);

        System.out.println(">> [Seed] Create 18 partite.");
        System.out.println(">> [Seed] Completato con successo!");
    }

    // ==================== HELPER ====================

    private Squadra creaSquadra(String nome, String citta, int annoFondazione,
                                 String coloriSociali, String logoUrl) {
        // idempotenza: se esiste gia', riusala
        return squadraRepository.findByNomeContainingIgnoreCase(nome).stream()
                .filter(s -> s.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseGet(() -> {
                    Squadra s = new Squadra();
                    s.setNome(nome);
                    s.setCitta(citta);
                    s.setAnnoFondazione(annoFondazione);
                    s.setColoriSociali(coloriSociali);
                    s.setLogoPath(logoUrl);
                    return squadraRepository.save(s);
                });
    }

    private Arbitro creaArbitro(String nome, String cognome, String codice) {
        return arbitroRepository.findByCodiceArbitrale(codice).orElseGet(() -> {
            Arbitro a = new Arbitro();
            a.setNome(nome);
            a.setCognome(cognome);
            a.setCodiceArbitrale(codice);
            return arbitroRepository.save(a);
        });
    }

    private void creaPartita(Torneo torneo, Squadra home, Squadra away, Arbitro arbitro,
                              String luogo, int giorniDaOggi,
                              Integer gh, Integer ga, Partita.Stato stato) {
        Partita p = new Partita();
        p.setTorneo(torneo);
        p.setSquadraHome(home);
        p.setSquadraAway(away);
        p.setArbitro(arbitro);
        p.setLuogo(luogo);
        p.setDataOra(LocalDateTime.now().plusDays(giorniDaOggi).withHour(15).withMinute(0));
        p.setGoalsHome(gh);
        p.setGoalsAway(ga);
        p.setStato(stato);
        partitaRepository.save(p);
    }
}
