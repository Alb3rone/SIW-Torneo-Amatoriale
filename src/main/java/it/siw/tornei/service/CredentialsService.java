package it.siw.tornei.service;

import it.siw.tornei.model.Credentials;
import it.siw.tornei.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CredentialsService {

    @Autowired private CredentialsRepository credentialsRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<Credentials> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }

    @Transactional
    public Credentials save(Credentials c) {
        if (c.getRole() == null) c.setRole(Credentials.USER_ROLE);
        c.setPassword(passwordEncoder.encode(c.getPassword()));
        return credentialsRepository.save(c);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String u) {
        return credentialsRepository.existsByUsername(u);
    }
}
