package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "partite")
public class Partita {

    public enum Stato { SCHEDULED, PLAYED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra;

    @NotBlank @Size(max = 100)
    @Column(nullable = false)
    private String luogo;

    @Min(0)
    @Column(name = "goals_home")
    private Integer goalsHome;

    @Min(0)
    @Column(name = "goals_away")
    private Integer goalsAway;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Stato stato = Stato.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "squadra_home_id", nullable = false)
    private Squadra squadraHome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "squadra_away_id", nullable = false)
    private Squadra squadraAway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;

    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commento> commenti = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }
    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    public Integer getGoalsHome() { return goalsHome; }
    public void setGoalsHome(Integer goalsHome) { this.goalsHome = goalsHome; }
    public Integer getGoalsAway() { return goalsAway; }
    public void setGoalsAway(Integer goalsAway) { this.goalsAway = goalsAway; }
    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public Squadra getSquadraHome() { return squadraHome; }
    public void setSquadraHome(Squadra squadraHome) { this.squadraHome = squadraHome; }
    public Squadra getSquadraAway() { return squadraAway; }
    public void setSquadraAway(Squadra squadraAway) { this.squadraAway = squadraAway; }
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    public List<Commento> getCommenti() { return commenti; }
    public void setCommenti(List<Commento> commenti) { this.commenti = commenti; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Partita p)) return false;
        return Objects.equals(id, p.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
