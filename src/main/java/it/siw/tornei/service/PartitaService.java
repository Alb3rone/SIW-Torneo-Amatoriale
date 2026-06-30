package it.siw.tornei.service;

import it.siw.tornei.model.Partita;
import it.siw.tornei.repository.PartitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PartitaService {

    @Autowired private PartitaRepository partitaRepository;

    @Transactional(readOnly = true)
    public List<Partita> findAll() { return partitaRepository.findAllWithRelations(); }

    @Transactional(readOnly = true)
    public Optional<Partita> findById(Long id) { return partitaRepository.findByIdWithRelations(id); }

    @Transactional(readOnly = true)
    public List<Partita> findByTorneo(Long torneoId) {
        return partitaRepository.findByTorneoIdWithJoinFetch(torneoId);
    }

    @Transactional(readOnly = true)
    public Partita findByIdWithCommenti(Long id) {
        // Carica prima la partita con tutte le relazioni @ManyToOne
        // (non si possono fare 2 JOIN FETCH su collezioni nella stessa query)
        return partitaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new IllegalArgumentException("Partita non trovata: " + id));
    }

    @Transactional
    public Partita save(Partita p) { return partitaRepository.save(p); }

    @Transactional
    public Partita registraRisultato(Long id, int goalsHome, int goalsAway) {
        Partita p = partitaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));
        if (goalsHome < 0 || goalsAway < 0) {
            throw new IllegalArgumentException("I gol non possono essere negativi");
        }
        p.setGoalsHome(goalsHome);
        p.setGoalsAway(goalsAway);
        p.setStato(Partita.Stato.PLAYED);
        return p;
    }

    @Transactional
    public void deleteById(Long id) { partitaRepository.deleteById(id); }
}
