package it.siw.tornei.service;

import it.siw.tornei.model.Giocatore;
import it.siw.tornei.repository.GiocatoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GiocatoreService {

    @Autowired private GiocatoreRepository giocatoreRepository;

    @Transactional(readOnly = true)
    public List<Giocatore> findAll() { return giocatoreRepository.findAllWithSquadra(); }

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
