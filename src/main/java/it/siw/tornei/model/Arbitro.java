package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "arbitri")
public class Arbitro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String nome;

    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String cognome;

    @NotBlank @Size(min = 3, max = 30)
    @Column(name = "codice_arbitrale", nullable = false, unique = true)
    private String codiceArbitrale;

    @OneToMany(mappedBy = "arbitro")
    private List<Partita> partite = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getCodiceArbitrale() { return codiceArbitrale; }
    public void setCodiceArbitrale(String codiceArbitrale) { this.codiceArbitrale = codiceArbitrale; }
    public List<Partita> getPartite() { return partite; }
    public void setPartite(List<Partita> partite) { this.partite = partite; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arbitro a)) return false;
        return Objects.equals(id, a.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
