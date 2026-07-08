package it.siw.tornei.dto;

import it.siw.tornei.model.Giocatore;

/**
 * DTO = Data Transfer Object.
 *
 * Versione "piatta" di Giocatore, pensata per essere serializzata in JSON
 * e mandata al frontend React (lista paginata dei giocatori).
 *
 * Perche' non passiamo direttamente l'Entity:
 *   - Squadra e' LAZY: Jackson prova ad accedere e puo' esplodere con
 *     LazyInitializationException (dipende da open-in-view).
 *   - Relazioni bidirezionali (Giocatore -> Squadra -> Giocatori -> ...)
 *     causerebbero cicli infiniti nella serializzazione JSON.
 *   - Esponiamo SOLO i campi che al frontend servono davvero
 *     (id per il link al dettaglio, nome/cognome/ruolo/altezza per la card,
 *     nomeSquadra per l'etichetta).
 *
 * Pattern: metodo statico from(entity) che fa la conversione.
 * Nessun setter: il DTO si popola solo via from(...), cosi' e' "immutabile
 * dopo la costruzione" e nessuno lo puo' modificare per sbaglio.
 */
public class GiocatoreDTO {

    private Long id;
    private String nome;
    private String cognome;
    private String ruolo;         // enum serializzato come String
    private Integer altezza;
    private String nomeSquadra;   // gia' concatenato: evita di esporre Squadra

    public static GiocatoreDTO from(Giocatore g) {
        GiocatoreDTO d = new GiocatoreDTO();
        d.id = g.getId();
        d.nome = g.getNome();
        d.cognome = g.getCognome();
        d.ruolo = g.getRuolo() != null ? g.getRuolo().name() : null;
        d.altezza = g.getAltezza();
        d.nomeSquadra = g.getSquadra() != null ? g.getSquadra().getNome() : null;
        return d;
    }

    // ========== Getter (no setter) ==========
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getRuolo() { return ruolo; }
    public Integer getAltezza() { return altezza; }
    public String getNomeSquadra() { return nomeSquadra; }
}
