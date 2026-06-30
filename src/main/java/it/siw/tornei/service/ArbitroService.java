package it.siw.tornei.service;

import it.siw.tornei.model.Arbitro;
import it.siw.tornei.repository.ArbitroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArbitroService {

    @Autowired private ArbitroRepository arbitroRepository;

    @Transactional(readOnly = true)
    public List<Arbitro> findAll() { return arbitroRepository.findAll(); }

    @Transactional(readOnly = true)
    public Optional<Arbitro> findById(Long id) { return arbitroRepository.findById(id); }

    @Transactional
    public Arbitro save(Arbitro a) {
        if (a.getId() == null && arbitroRepository.existsByCodiceArbitrale(a.getCodiceArbitrale())) {
            throw new IllegalArgumentException("Codice arbitrale gia' presente: " + a.getCodiceArbitrale());
        }
        return arbitroRepository.save(a);
    }

    @Transactional
    public void deleteById(Long id) { arbitroRepository.deleteById(id); }
}
