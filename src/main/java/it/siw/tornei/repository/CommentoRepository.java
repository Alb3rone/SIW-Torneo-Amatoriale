package it.siw.tornei.repository;

import it.siw.tornei.model.Commento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentoRepository extends JpaRepository<Commento, Long> {

    @Query("SELECT c FROM Commento c JOIN FETCH c.autore WHERE c.partita.id = :partitaId ORDER BY c.dataCreazione DESC")
    List<Commento> findByPartitaIdWithAutore(Long partitaId);

    List<Commento> findByAutoreId(Long autoreId);

    boolean existsByAutoreIdAndPartitaId(Long autoreId, Long partitaId);
}
