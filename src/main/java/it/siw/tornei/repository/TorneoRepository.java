package it.siw.tornei.repository;

import it.siw.tornei.model.Torneo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    List<Torneo> findByAnno(Integer anno);

    List<Torneo> findByNomeContainingIgnoreCase(String nome);

    /**
     * Fetch del torneo con squadre - usa JOIN FETCH per evitare N+1.
     * Caso d'uso: pagina dettaglio torneo che mostra le squadre.
     */
    @Query("SELECT DISTINCT t FROM Torneo t LEFT JOIN FETCH t.squadre WHERE t.id = :id")
    Optional<Torneo> findByIdWithSquadre(Long id);

    /**
     * Versione con EntityGraph alternativa.
     */
    @EntityGraph(attributePaths = {"squadre", "partite"})
    Optional<Torneo> findWithEntityGraphById(Long id);
}
