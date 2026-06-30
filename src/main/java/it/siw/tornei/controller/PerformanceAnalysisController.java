package it.siw.tornei.controller;

import it.siw.tornei.repository.PartitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Analisi sperimentale richiesta dalla Sezione 8.2 del PDF.
 * Confronta tre strategie di fetch per il caso d'uso "visualizza partite di un torneo":
 *  - LAZY  (default, causa N+1)
 *  - JOIN FETCH (1 sola query)
 *  - EntityGraph (equivalente a join fetch ma dichiarativo)
 *
 * Endpoint riservato all'amministratore.
 */
@RestController
@RequestMapping("/admin/perf")
public class PerformanceAnalysisController {

    @Autowired private PartitaRepository partitaRepository;

    @GetMapping("/fetch-strategies/{torneoId}")
    @Transactional(readOnly = true)
    public Map<String, Object> confronta(@PathVariable Long torneoId) {
        Map<String, Object> risultati = new HashMap<>();

        long t0 = System.nanoTime();
        var partiteLazy = partitaRepository.findByTorneoIdOrderByDataOraAsc(torneoId);
        // accesso che forza il caricamento -> qui esplode N+1
        partiteLazy.forEach(p -> {
            p.getSquadraHome().getNome();
            p.getSquadraAway().getNome();
        });
        long t1 = System.nanoTime();
        risultati.put("LAZY_ms", (t1 - t0) / 1_000_000.0);
        risultati.put("LAZY_partite", partiteLazy.size());

        long t2 = System.nanoTime();
        var joinFetch = partitaRepository.findByTorneoIdWithJoinFetch(torneoId);
        joinFetch.forEach(p -> { p.getSquadraHome().getNome(); p.getSquadraAway().getNome(); });
        long t3 = System.nanoTime();
        risultati.put("JOIN_FETCH_ms", (t3 - t2) / 1_000_000.0);

        long t4 = System.nanoTime();
        var graph = partitaRepository.findByTorneoIdWithEntityGraph(torneoId);
        graph.forEach(p -> { p.getSquadraHome().getNome(); p.getSquadraAway().getNome(); });
        long t5 = System.nanoTime();
        risultati.put("ENTITY_GRAPH_ms", (t5 - t4) / 1_000_000.0);

        risultati.put("commento",
            "LAZY -> 1 query per le partite + N query per ciascuna squadra (N+1). " +
            "JOIN_FETCH -> 1 sola query. ENTITY_GRAPH -> equivalente, ma dichiarativo.");
        return risultati;
    }
}
