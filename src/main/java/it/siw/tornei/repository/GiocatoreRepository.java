package it.siw.tornei.repository;

import it.siw.tornei.model.Giocatore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {
    List<Giocatore> findBySquadraId(Long squadraId);
    List<Giocatore> findByRuolo(Giocatore.Ruolo ruolo);
    List<Giocatore> findByCognomeContainingIgnoreCase(String cognome);

    // Carica giocatore + squadra in un'unica query (evita LazyInitializationException
    // con open-in-view=false e previene il problema N+1 nelle viste).
    @Query("SELECT g FROM Giocatore g JOIN FETCH g.squadra WHERE g.id = :id")
    Optional<Giocatore> findByIdWithSquadra(Long id);

    @Query("SELECT g FROM Giocatore g JOIN FETCH g.squadra ORDER BY g.cognome")
    List<Giocatore> findAllWithSquadra();

    /**
     * Versione paginata di findAllWithSquadra, per il modulo React con
     * "1 2 3 ..." di navigazione tra pagine.
     *
     * Come funziona:
     *   - Il parametro Pageable porta con se' page, size e sort. Spring lo
     *     traduce in "LIMIT size OFFSET (page*size)" nella query SQL generata.
     *   - Ritorniamo Page<T> invece di List<T>: e' una lista PIU' i metadati
     *     (totalPages, totalElements, numero pagina corrente) che al frontend
     *     servono per disegnare i bottoni della paginazione.
     *   - JOIN FETCH su squadra e' sicuro qui perche' e' una @ManyToOne (relazione
     *     singola). Con @OneToMany, invece, Hibernate emetterebbe il warning
     *     HHH000104 e paginerebbe in memoria (rischio OutOfMemory).
     *   - Il countQuery separato serve a Spring per calcolare totalPages senza
     *     eseguire il JOIN FETCH (che nel COUNT non serve, sarebbe piu' lento).
     *   - Non mettiamo ORDER BY nella @Query: l'ordinamento arriva dal Pageable
     *     (Sort) inviato dal client. Cosi' e' flessibile.
     */
    @Query(
        value = "SELECT g FROM Giocatore g JOIN FETCH g.squadra",
        countQuery = "SELECT COUNT(g) FROM Giocatore g"
    )
    Page<Giocatore> findAllWithSquadraPaged(Pageable pageable);

    long countByAltezzaGreaterThan(Integer altezza);
}
