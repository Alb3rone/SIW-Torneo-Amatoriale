package it.siw.tornei.service;

import it.siw.tornei.model.Giocatore;
import it.siw.tornei.repository.GiocatoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GiocatoreService {

    @Autowired private GiocatoreRepository giocatoreRepository;

    @Transactional(readOnly = true)
    public List<Giocatore> findAll() { return giocatoreRepository.findAllWithSquadra(); }

    /**
     * Ritorna una pagina di giocatori (con squadra gia' caricata via JOIN FETCH).
     * Il chiamante decide page/size/sort costruendo il Pageable.
     * Esempio d'uso: PageRequest.of(0, 15, Sort.by("cognome").ascending())
     */
    @Transactional(readOnly = true)
    public Page<Giocatore> findAllPaged(Pageable pageable) {
        return giocatoreRepository.findAllWithSquadraPaged(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Giocatore> findById(Long id) { return giocatoreRepository.findByIdWithSquadra(id); }

    @Transactional(readOnly = true)
    public List<Giocatore> findBySquadra(Long squadraId) {
        return giocatoreRepository.findBySquadraId(squadraId);
    }

    @Transactional
    public Giocatore save(Giocatore g) { return giocatoreRepository.save(g); }

    @Transactional
    public void deleteById(Long id) { giocatoreRepository.deleteById(id); }

    public long contaGiocatorePiuAltiDi(Integer altezza){
        return giocatoreRepository.countByAltezzaGreaterThan(altezza);
    }

}
