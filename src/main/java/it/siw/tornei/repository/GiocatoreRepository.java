package it.siw.tornei.repository;

import it.siw.tornei.model.Giocatore;
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
}
