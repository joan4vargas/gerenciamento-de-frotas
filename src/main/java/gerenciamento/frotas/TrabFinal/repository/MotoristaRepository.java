package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.Motorista;
import gerenciamento.frotas.TrabFinal.model.entity.StatusMotorista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    Optional<Motorista> findByCpf(String cpf);
    Optional<Motorista> findByCnh(String cnh);
    List<Motorista> findByStatus(StatusMotorista status);
    Page<Motorista> findByStatus(StatusMotorista status, Pageable pageable);
    boolean existsByCpf(String cpf);
    boolean existsByCnh(String cnh);
}