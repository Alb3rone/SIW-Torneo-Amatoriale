package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "squadre")
public class Squadra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 2, max = 80)
    @Column(nullable = false)
    private String nome;

    @NotNull @Min(1800) @Max(2100)
    @Column(name = "anno_fondazione", nullable = false)
    private Integer annoFondazione;

    @NotBlank @Size(max = 80)
    @Column(nullable = false)
    private String citta;

    // Logo/stemma della squadra. Salvato come URL (es. "/uploads/uuid.png").
    // Caricato via FileStorageService quando admin sottomette il form.
    @Size(max = 500)
    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Size(max = 30)
    @Column(name = "colori_sociali", length = 30)
    private String coloriSociali;

    @ManyToMany(mappedBy = "squadre")
    private Set<Torneo> tornei = new HashSet<>();

    @OneToMany(mappedBy = "squadra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Giocatore> giocatori = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnnoFondazione() { return annoFondazione; }
    public void setAnnoFondazione(Integer annoFondazione) { this.annoFondazione = annoFondazione; }
    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    public String getColoriSociali() { return coloriSociali; }
    public void setColoriSociali(String coloriSociali) { this.coloriSociali = coloriSociali; }
    public Set<Torneo> getTornei() { return tornei; }
    public void setTornei(Set<Torneo> tornei) { this.tornei = tornei; }
    public List<Giocatore> getGiocatori() { return giocatori; }
    public void setGiocatori(List<Giocatore> giocatori) { this.giocatori = giocatori; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Squadra s)) return false;
        return Objects.equals(id, s.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
