package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "giocatori")
public class Giocatore {

    public enum Ruolo { PORTIERE, DIFENSORE, CENTROCAMPISTA, ATTACCANTE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String nome;

    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String cognome;

    @NotNull @Past
    @Column(name = "data_nascita", nullable = false)
    private LocalDate dataNascita;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Ruolo ruolo;

    @NotNull @Min(140) @Max(220)
    @Column(nullable = false)
    private Integer altezza;

    // Ogni giocatore appartiene a una sola squadra
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "squadra_id", nullable = false)
    private Squadra squadra;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }
    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }
    public Integer getAltezza() { return altezza; }
    public void setAltezza(Integer altezza) { this.altezza = altezza; }
    public Squadra getSquadra() { return squadra; }
    public void setSquadra(Squadra squadra) { this.squadra = squadra; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Giocatore g)) return false;
        return Objects.equals(id, g.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
