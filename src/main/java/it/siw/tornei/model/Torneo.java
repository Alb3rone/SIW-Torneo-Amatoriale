package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "tornei")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String nome;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer anno;

    @Size(max = 2000)
    @Column(length = 2000)
    private String descrizione;

    // Relazioni
    @ManyToMany
    @JoinTable(name = "torneo_squadre",
            joinColumns = @JoinColumn(name = "torneo_id"),
            inverseJoinColumns = @JoinColumn(name = "squadra_id"))
    private Set<Squadra> squadre = new HashSet<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partita> partite = new ArrayList<>();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Set<Squadra> getSquadre() { return squadre; }
    public void setSquadre(Set<Squadra> squadre) { this.squadre = squadre; }
    public List<Partita> getPartite() { return partite; }
    public void setPartite(List<Partita> partite) { this.partite = partite; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Torneo t)) return false;
        return Objects.equals(id, t.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
