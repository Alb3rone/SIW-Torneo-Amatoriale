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

    @Autowired private CommentoRepository commentoRepository;
    @Autowired private PartitaRepository partitaRepository;
    @Autowired private CredentialsRepository credentialsRepository;

    @Transactional(readOnly = true)
    public List<Commento> findByPartita(Long partitaId) {
        return commentoRepository.findByPartitaIdWithAutore(partitaId);
    }

    @Transactional
    public Commento creaCommento(Long partitaId, String testo, String username) {
        Partita partita = partitaRepository.findById(partitaId)
                .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));
        Credentials cred = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        Commento c = new Commento();
        c.setPartita(partita);
        c.setAutore(cred.getUtente());
        c.setTesto(testo);
        c.setDataCreazione(LocalDateTime.now());
        return commentoRepository.save(c);
    }

    @Transactional
    public Commento modificaCommento(Long commentoId, String nuovoTesto, String username) {
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
