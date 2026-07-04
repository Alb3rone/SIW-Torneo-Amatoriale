package it.siw.tornei.config;

import it.siw.tornei.model.*;
import it.siw.tornei.repository.CredentialsRepository;
import it.siw.tornei.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Crea gli utenti di test al primo avvio.
 * Include admin, user generico e 5 utenti extra per testare i commenti sulle partite.
 * Tornei, squadre, partite ecc. li popola DataSeeder (Order=2).
 */
@Component
public class DataBootstrap implements CommandLineRunner {

    @Autowired private CredentialsService credentialsService;
    @Autowired private CredentialsRepository credentialsRepository;

    @Override
    public void run(String... args) {
        // ==================== UTENTI DI SISTEMA ====================
        creaUtente("admin", "admin123", Credentials.ADMIN_ROLE,
                "Amministratore", "Sistema", "admin@siw.it");
        creaUtente("user", "user123", Credentials.USER_ROLE,
                "Mario", "Rossi", "user@siw.it");

        // ==================== UTENTI DI TEST (per commenti) ====================
        creaUtente("marco.bianchi",  "marco123",  Credentials.USER_ROLE,
                "Marco",  "Bianchi",  "marco.bianchi@gmail.com");
        creaUtente("giulia.verdi",   "giulia123", Credentials.USER_ROLE,
                "Giulia", "Verdi",    "giulia.verdi@yahoo.it");
        creaUtente("luca.rossi",     "luca123",   Credentials.USER_ROLE,
                "Luca",   "Rossi",    "luca.rossi@hotmail.com");
        creaUtente("sara.neri",      "sara123",   Credentials.USER_ROLE,
                "Sara",   "Neri",     "sara.neri@email.it");
        creaUtente("paolo.gialli",   "paolo123",  Credentials.USER_ROLE,
                "Paolo",  "Gialli",   "paolo.gialli@gmail.com");
    }

    private void creaUtente(String username, String password, String role,
                            String nome, String cognome, String email) {
        if (credentialsRepository.existsByUsername(username)) return;
        Credentials c = new Credentials();
        c.setUsername(username);
        c.setPassword(password);
        c.setRole(role);
        Utente u = new Utente();
        u.setNome(nome);
        u.setCognome(cognome);
        u.setEmail(email);
        c.setUtente(u);
        credentialsService.save(c);
        System.out.println(">> Creato utente " + username + " / " + password + " (" + role + ")");
    }
}
