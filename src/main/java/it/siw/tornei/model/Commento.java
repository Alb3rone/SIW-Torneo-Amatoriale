package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "commenti")
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 1, max = 1000)
    @Column(nullable = false, length = 1000)
    private String testo;

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partita_id", nullable = false)
    private Partita partita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autore_id", nullable = false)
    private Utente autore;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }
    public Partita getPartita() { return partita; }
    public void setPartita(Partita partita) { this.partita = partita; }
    public Utente getAutore() { return autore; }
    public void setAutore(Utente autore) { this.autore = autore; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commento c)) return false;
        return Objects.equals(id, c.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
