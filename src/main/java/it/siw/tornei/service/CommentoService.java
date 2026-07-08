package it.siw.tornei.service;

import it.siw.tornei.model.*;
import it.siw.tornei.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentoService {

    @Autowired
    private CommentoRepository commentoRepository;
    @Autowired
    private PartitaRepository partitaRepository;
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Transactional(readOnly = true)
    public List<Commento> findByPartita(Long partitaId) {
        return commentoRepository.findByPartitaIdWithAutore(partitaId);
    }

    @Transactional
    public Commento creaCommento(Long partitaId, String testo, Integer voto, String username) {
        Partita partita = partitaRepository.findById(partitaId)
                .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));
        Credentials cred = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        boolean giaEsistente = commentoRepository.existsByAutoreIdAndPartitaId(cred.getUtente().getId(), partitaId);
        if (giaEsistente) {
            throw new IllegalArgumentException("Hai già inserito un commento per questa partita.");
        }
        Commento c = new Commento();
        c.setPartita(partita);
        c.setAutore(cred.getUtente());
        c.setTesto(testo);
        c.setDataCreazione(LocalDateTime.now());
        // Voto: opzionale. Se presente deve essere in [1,5]; blocchiamo qui
        // valori fuori range come prima difesa (la seconda e' @Min/@Max sull'entita').
        if (voto != null && voto >= 1 && voto <= 5) {
            c.setVoto(voto);
        }
        return commentoRepository.save(c);
    }

    @Transactional
    public Commento modificaCommento(Long commentoId, String nuovoTesto, Integer nuovoVoto, String username) {
        Commento c = commentoRepository.findById(commentoId)
                .orElseThrow(() -> new IllegalArgumentException("Commento non trovato"));
        Credentials cred = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        // Solo l'autore puo' modificare i propri commenti
        if (!c.getAutore().getId().equals(cred.getUtente().getId())
                && !Credentials.ADMIN_ROLE.equals(cred.getRole())) {
            throw new AccessDeniedException("Non puoi modificare commenti di altri utenti");
        }
        c.setTesto(nuovoTesto);
        // Aggiorna il voto solo se fornito e valido. Se l'utente non seleziona
        // il voto in modifica, lasciamo quello esistente (comportamento meno
        // sorprendente rispetto a "azzerarlo").
        if (nuovoVoto != null && nuovoVoto >= 1 && nuovoVoto <= 5) {
            c.setVoto(nuovoVoto);
        }
        return c;
    }

    @Transactional
    public void elimina(Long commentoId, String username) {
        Commento c = commentoRepository.findById(commentoId)
                .orElseThrow(() -> new IllegalArgumentException("Commento non trovato"));
        Credentials cred = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        if (!c.getAutore().getId().equals(cred.getUtente().getId())
                && !Credentials.ADMIN_ROLE.equals(cred.getRole())) {
            throw new AccessDeniedException("Non puoi eliminare commenti di altri utenti");
        }
        commentoRepository.delete(c);
    }
}
