package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.veiculo.StatusVeiculo;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
    Optional<Veiculo> findByChassi(String chassi);
    List<Veiculo> findByStatus(StatusVeiculo status);
    Page<Veiculo> findByStatus(StatusVeiculo status, Pageable pageable);
    boolean existsByPlaca(String placa);
    boolean existsByChassi(String chassi);
}