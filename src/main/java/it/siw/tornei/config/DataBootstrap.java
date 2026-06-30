package it.siw.tornei.config;

import it.siw.tornei.model.*;
import it.siw.tornei.repository.CredentialsRepository;
import it.siw.tornei.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Crea SOLO gli utenti di test (admin / user) al primo avvio.
 * Tornei, squadre, partite ecc. li popoli tu dal sito.
 */
@Component
public class DataBootstrap implements CommandLineRunner {

    @Autowired private CredentialsService credentialsService;
    @Autowired private CredentialsRepository credentialsRepository;

    @Override
    public void run(String... args) {
        if (!credentialsRepository.existsByUsername("admin")) {
            Credentials admin = new Credentials();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole(Credentials.ADMIN_ROLE);
            Utente u = new Utente();
            u.setNome("Amministratore");
            u.setCognome("Sistema");
            u.setEmail("admin@siw.it");
            admin.setUtente(u);
            credentialsService.save(admin);
            System.out.println(">> Creato utente admin / admin123 (ADMIN)");
        }
        if (!credentialsRepository.existsByUsername("user")) {
            Credentials user = new Credentials();
            user.setUsername("user");
            user.setPassword("user123");
            user.setRole(Credentials.USER_ROLE);
            Utente u = new Utente();
            u.setNome("Mario");
            u.setCognome("Rossi");
            u.setEmail("user@siw.it");
            user.setUtente(u);
            credentialsService.save(user);
            System.out.println(">> Creato utente user / user123 (USER)");
        }
    }
}
