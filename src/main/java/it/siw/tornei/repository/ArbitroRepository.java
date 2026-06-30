package it.siw.tornei.repository;

import it.siw.tornei.model.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {
    Optional<Arbitro> findByCodiceArbitrale(String codice);
    boolean existsByCodiceArbitrale(String codice);
}
