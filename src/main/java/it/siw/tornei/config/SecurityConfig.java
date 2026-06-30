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
                .permitAll())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }
}
