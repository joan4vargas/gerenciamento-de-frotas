package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    List<Viagem> findByMotoristaId(Long motoristaId);
    List<Viagem> findByVeiculoId(Long veiculoId);
    Page<Viagem> findByStatusViagem(Boolean status, Pageable pageable);

    // Regra de negócio: verifica se motorista já está em viagem ativa
    @Query("SELECT COUNT(v) > 0 FROM Viagem v WHERE v.motorista.id = :motoristaId AND v.statusViagem = false")
    boolean existsViagemAtivaByMotorista(@Param("motoristaId") Long motoristaId);

    // Regra de negócio: verifica se veículo já está em viagem ativa
    @Query("SELECT COUNT(v) > 0 FROM Viagem v WHERE v.veiculo.id = :veiculoId AND v.statusViagem = false")
    boolean existsViagemAtivaByVeiculo(@Param("veiculoId") Long veiculoId);

    // Relatório: viagens por motorista
    @Query("SELECT v FROM Viagem v WHERE v.motorista.id = :motoristaId ORDER BY v.dataPartida DESC")
    Page<Viagem> findViagensByMotorista(@Param("motoristaId") Long motoristaId, Pageable pageable);
}