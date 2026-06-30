package it.siw.tornei.repository;

import it.siw.tornei.model.Partita;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartitaRepository extends JpaRepository<Partita, Long> {

    /**
     * Versione LAZY (default): provoca il problema N+1 quando si itera sulle squadre.
     */
    List<Partita> findByTorneoIdOrderByDataOraAsc(Long torneoId);

    /**
     * Versione con JOIN FETCH: una sola query, niente N+1.
     * Caso d'uso usato nell'analisi sperimentale (Sezione 8.2 del PDF).
     */
    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraHome " +
           "JOIN FETCH p.squadraAway " +
           "LEFT JOIN FETCH p.arbitro " +
           "WHERE p.torneo.id = :torneoId " +
           "ORDER BY p.dataOra ASC")
    List<Partita> findByTorneoIdWithJoinFetch(Long torneoId);

    /**
     * Versione con EntityGraph: stesso obiettivo del JOIN FETCH ma dichiarativo.
     */
    @EntityGraph(attributePaths = {"squadraHome", "squadraAway", "arbitro"})
    @Query("SELECT p FROM Partita p WHERE p.torneo.id = :torneoId ORDER BY p.dataOra ASC")
    List<Partita> findByTorneoIdWithEntityGraph(Long torneoId);

    @Query("SELECT p FROM Partita p LEFT JOIN FETCH p.commenti c LEFT JOIN FETCH c.autore WHERE p.id = :id")
    Optional<Partita> findByIdWithCommenti(Long id);

    /**
     * Carica una partita con tutte le sue relazioni @ManyToOne in un'unica query.
     * Usata da controller e service per il dettaglio (template che accede a
     * partita.squadraHome.nome, .squadraAway.nome, .torneo.nome, .arbitro).
     */
    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraHome " +
           "JOIN FETCH p.squadraAway " +
           "JOIN FETCH p.torneo " +
           "LEFT JOIN FETCH p.arbitro " +
           "WHERE p.id = :id")
    Optional<Partita> findByIdWithRelations(Long id);

    /**
     * Tutte le partite con relazioni @ManyToOne caricate eagerly (per la lista).
     */
    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraHome " +
           "JOIN FETCH p.squadraAway " +
           "JOIN FETCH p.torneo " +
           "LEFT JOIN FETCH p.arbitro " +
           "ORDER BY p.dataOra DESC")
    List<Partita> findAllWithRelations();

    long countByStato(Partita.Stato stato);
}
