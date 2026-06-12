package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Caminhao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaminhaoRepository extends JpaRepository<Caminhao, Long> {
    List<Caminhao> findByMarca(String marca);
    List<Caminhao> findByTipoCaminhao(String tipoCaminhao);
}