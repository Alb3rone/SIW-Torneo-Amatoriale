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
}
