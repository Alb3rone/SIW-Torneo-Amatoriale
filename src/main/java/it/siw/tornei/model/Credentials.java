package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "credentials")
public class Credentials {

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 3, max = 30)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String role = USER_ROLE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credentials c)) return false;
        return Objects.equals(id, c.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
