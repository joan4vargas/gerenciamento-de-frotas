package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Furgao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FurgaoRepository extends JpaRepository<Furgao, Long> {
    List<Furgao> findByMarca(String marca);
}