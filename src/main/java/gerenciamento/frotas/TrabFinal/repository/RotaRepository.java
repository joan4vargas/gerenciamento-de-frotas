package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.Rota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RotaRepository extends JpaRepository<Rota, Long> {
    List<Rota> findByOrigem(String origem);
    List<Rota> findByDestino(String destino);
    Optional<Rota> findByOrigemAndDestino(String origem, String destino);
    Page<Rota> findByOrigemContainingIgnoreCase(String origem, Pageable pageable);
}