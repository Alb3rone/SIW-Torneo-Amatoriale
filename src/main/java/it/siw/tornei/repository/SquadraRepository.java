package it.siw.tornei.repository;

import it.siw.tornei.model.Squadra;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SquadraRepository extends JpaRepository<Squadra, Long> {

    List<Squadra> findByCittaIgnoreCase(String citta);

    List<Squadra> findByNomeContainingIgnoreCase(String nome);

    /**
     * Carica squadra + giocatori + tornei in un'unica operazione.
     * Hibernate non supporta 2 JOIN FETCH su collezioni separate nella stessa query,
     * quindi qui usiamo solo giocatori (la collezione iterata nel template).
     * Per i tornei usiamo un EntityGraph che fa una seconda query separata.
     */
    @EntityGraph(attributePaths = {"tornei"})
    @Query("SELECT s FROM Squadra s LEFT JOIN FETCH s.giocatori WHERE s.id = :id")
    Optional<Squadra> findByIdWithGiocatori(Long id);

    @Query("SELECT s FROM Squadra s JOIN s.tornei t WHERE t.id = :torneoId")
    List<Squadra> findByTorneoId(Long torneoId);

    /**
     * Carica TUTTE le squadre con TUTTI i giocatori in una singola query.
     * Usato dall'analisi sperimentale (sezione 8.2) per il confronto con il
     * caricamento LAZY (che genera N+1).
     *
     * DISTINCT: serve perche' il JOIN restituisce righe duplicate della squadra
     *           per ogni giocatore, e vogliamo squadre uniche.
     * LEFT JOIN FETCH: LEFT per non escludere squadre senza giocatori;
     *                  FETCH per caricare la collezione nella stessa query.
     */
    @Query("SELECT DISTINCT s FROM Squadra s LEFT JOIN FETCH s.giocatori")
    List<Squadra> findAllWithGiocatori();
}
