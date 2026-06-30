package it.siw.tornei.service;

import it.siw.tornei.dto.RigaClassificaDTO;
import it.siw.tornei.model.*;
import it.siw.tornei.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TorneoService {

    @Autowired private TorneoRepository torneoRepository;
    @Autowired private PartitaRepository partitaRepository;

    @Transactional(readOnly = true)
    public List<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Torneo> findById(Long id) {
        return torneoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Torneo findByIdWithSquadre(Long id) {
        return torneoRepository.findByIdWithSquadre(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato: " + id));
    }

    @Transactional
    public Torneo save(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    @Transactional
    public void deleteById(Long id) {
        torneoRepository.deleteById(id);
    }

    /**
     * Caso d'uso che attraversa piu' entita': calcola la classifica del torneo
     * a partire dalle partite PLAYED. Usa la query JOIN FETCH per evitare N+1.
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<RigaClassificaDTO> calcolaClassifica(Long torneoId) {
        List<Partita> partite = partitaRepository.findByTorneoIdWithJoinFetch(torneoId);
        Map<Long, RigaClassificaDTO> mappa = new LinkedHashMap<>();

        for (Partita p : partite) {
            if (p.getStato() != Partita.Stato.PLAYED) continue;
            Squadra h = p.getSquadraHome();
            Squadra a = p.getSquadraAway();
            RigaClassificaDTO rh = mappa.computeIfAbsent(h.getId(), k -> new RigaClassificaDTO(h.getId(), h.getNome()));
            RigaClassificaDTO ra = mappa.computeIfAbsent(a.getId(), k -> new RigaClassificaDTO(a.getId(), a.getNome()));
            int gh = p.getGoalsHome() != null ? p.getGoalsHome() : 0;
            int ga = p.getGoalsAway() != null ? p.getGoalsAway() : 0;
            rh.aggiungiPartita(gh, ga, true);
            ra.aggiungiPartita(gh, ga, false);
        }

        List<RigaClassificaDTO> classifica = new ArrayList<>(mappa.values());
        classifica.sort(Comparator
                .comparingInt(RigaClassificaDTO::getPunti).reversed()
                .thenComparingInt(RigaClassificaDTO::getDifferenzaReti).reversed()
                .thenComparingInt(RigaClassificaDTO::getGolFatti).reversed());
        return classifica;
    }
}
