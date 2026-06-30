package it.siw.tornei.service;

import it.siw.tornei.model.Squadra;
import it.siw.tornei.repository.SquadraRepository;
import it.siw.tornei.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class SquadraService {

    @Autowired private SquadraRepository squadraRepository;
    @Autowired private FileStorageService storage;

    @Transactional(readOnly = true)
    public List<Squadra> findAll() { return squadraRepository.findAll(); }

    @Transactional(readOnly = true)
    public Optional<Squadra> findById(Long id) { return squadraRepository.findById(id); }

    @Transactional(readOnly = true)
    public Squadra findByIdWithGiocatori(Long id) {
        return squadraRepository.findByIdWithGiocatori(id)
                .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata: " + id));
    }

    @Transactional(readOnly = true)
    public List<Squadra> findByTorneoId(Long torneoId) {
        return squadraRepository.findByTorneoId(torneoId);
    }

    @Transactional
    public Squadra save(Squadra squadra) { return squadraRepository.save(squadra); }

    /**
     * Salva la squadra. Se viene fornito un file di logo non vuoto:
     *  - cancella il vecchio file da disco (se esisteva)
     *  - salva il nuovo via FileStorageService
     *  - aggiorna logoPath sull'entita'
     */
    @Transactional
    public Squadra salvaConLogo(Squadra squadra, MultipartFile logoFile) {
        if (logoFile != null && !logoFile.isEmpty()) {
            // Se stiamo modificando, cancella il vecchio file da disco
            if (squadra.getId() != null) {
                squadraRepository.findById(squadra.getId())
                        .ifPresent(esistente -> storage.delete(esistente.getLogoPath()));
            }
            String webPath = storage.save(logoFile);
            squadra.setLogoPath(webPath);
        } else if (squadra.getId() != null) {
            // Niente nuovo file in upload: preserva il path esistente, altrimenti
            // verrebbe sovrascritto a null (perche' il form non lo invia esplicitamente)
            squadraRepository.findById(squadra.getId())
                    .ifPresent(esistente -> {
                        if (squadra.getLogoPath() == null) {
                            squadra.setLogoPath(esistente.getLogoPath());
                        }
                    });
        }
        return squadraRepository.save(squadra);
    }

    @Transactional
    public void deleteById(Long id) {
        // Pulizia file dal disco prima di eliminare la riga
        squadraRepository.findById(id).ifPresent(s -> storage.delete(s.getLogoPath()));
        squadraRepository.deleteById(id);
    }
}
