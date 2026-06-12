package gerenciamento.frotas.TrabFinal.repository;

import gerenciamento.frotas.TrabFinal.model.entity.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIdAndLidaFalse(Long usuarioId);
    Page<Notificacao> findByUsuarioId(Long usuarioId, Pageable pageable);
    long countByUsuarioIdAndLidaFalse(Long usuarioId);
}