package it.siw.tornei.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "commenti", uniqueConstraints = @UniqueConstraint(columnNames = { "partita_id", "autore_id" }))
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 1000)
    @Column(nullable = false, length = 1000)
    private String testo;

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    /**
     * Voto assegnato dall'utente alla partita, da 1 a 5 stelle.
     * Nullable perche':
     * - i commenti pre-esistenti nel DB (creati prima di questa feature)
     * non hanno un voto e devono continuare a essere validi.
     * - l'utente puo' volutamente pubblicare un commento "senza voto".
     * Se valorizzato deve stare nel range [1,5].
     */
    @Min(1)
    @Max(5)
    @Column(name = "voto") // nullable di default
    private Integer voto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partita_id", nullable = false)
    private Partita partita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autore_id", nullable = false)
    private Utente autore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Partita getPartita() {
        return partita;
    }

    public void setPartita(Partita partita) {
        this.partita = partita;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Commento c))
            return false;
        return Objects.equals(id, c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
