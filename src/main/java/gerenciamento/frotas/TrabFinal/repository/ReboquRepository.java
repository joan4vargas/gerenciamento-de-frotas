package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Reboque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReboquRepository extends JpaRepository<Reboque, Long> {
    List<Reboque> findByTipoReboque(String tipoReboque);
}