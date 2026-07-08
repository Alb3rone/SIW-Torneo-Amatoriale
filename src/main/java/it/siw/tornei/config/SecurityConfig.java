package it.siw.tornei.config;

import it.siw.tornei.model.Credentials;
import it.siw.tornei.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private CredentialsRepository credentialsRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Credentials c = credentialsRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
            return User.withUsername(c.getUsername())
                    .password(c.getPassword())
                    .roles(c.getRole())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/rest/**"))
            .authorizeHttpRequests(auth -> auth
                // statiche e pubbliche
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/uploads/**", "/webjars/**",
                                 "/login", "/register", "/error",
                                 "/tornei", "/tornei/**",
                                 "/squadre", "/squadre/**",
                                 "/giocatori", "/giocatori/**",
                                 "/arbitri", "/arbitri/**",
                                 "/partite", "/partite/**",
                                 "/rest/**",
                                 "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**", "/commenti/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID", "remember-me-siw")   // pulisce anche il cookie remember-me
                .permitAll())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            // ============================================================
            // Remember Me: dopo il login viene rilasciato un secondo cookie
            // firmato con la "key" qui sotto. Se JSESSIONID scade o il server
            // riparte, Spring usa il cookie remember-me per re-autenticare
            // silenziosamente l'utente (senza mai mostrare la pagina di login).
            //
            //   key                  chiave segreta usata per firmare il token.
            //                        NON deve cambiare tra un riavvio e l'altro,
            //                        altrimenti tutti i token gia' emessi
            //                        vengono invalidati.
            //   tokenValiditySeconds durata del cookie in secondi (qui 30 giorni).
            //   alwaysRemember       true = non serve una checkbox nel form di
            //                        login, il cookie viene rilasciato sempre.
            //   rememberMeCookieName nome del cookie visibile in devtools.
            // ============================================================
            .rememberMe(rm -> rm
                .key("siw-tornei-remember-me-key-2026")
                .tokenValiditySeconds(30 * 24 * 60 * 60)   // 30 giorni
                .alwaysRemember(true)
                .rememberMeCookieName("remember-me-siw")
            );
        return http.build();
    }
}
