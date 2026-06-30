package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 50)
    @Column(nullable = false)
    private String nome;

    @NotBlank @Size(max = 50)
    @Column(nullable = false)
    private String cognome;

    @Email @Size(max = 100)
    private String email;

    @OneToMany(mappedBy = "autore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commento> commenti = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Commento> getCommenti() { return commenti; }
    public void setCommenti(List<Commento> commenti) { this.commenti = commenti; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente u)) return false;
        return Objects.equals(id, u.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
