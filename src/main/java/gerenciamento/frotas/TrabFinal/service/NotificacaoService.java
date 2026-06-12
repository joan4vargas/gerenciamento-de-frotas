package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.notificacao.NotificacaoResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.Notificacao;
import gerenciamento.frotas.TrabFinal.repository.NotificacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<NotificacaoResponseDTO> listarNaoLidas(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndLidaFalse(usuarioId)
                .stream().map(this::toDTO).toList();
    }

    public Page<NotificacaoResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return notificacaoRepository.findByUsuarioId(usuarioId, pageable).map(this::toDTO);
    }

    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLida(Long id) {
        Notificacao n = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", id));
        n.setLida(true);
        notificacaoRepository.save(n);
    }

    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {
        notificacaoRepository.findByUsuarioIdAndLidaFalse(usuarioId).forEach(n -> {
            n.setLida(true);
            notificacaoRepository.save(n);
        });
    }

    public NotificacaoResponseDTO toDTO(Notificacao n) {
        NotificacaoResponseDTO dto = new NotificacaoResponseDTO();
        dto.setId(n.getId());
        dto.setMensagem(n.getMensagem());
        dto.setDataCriacao(n.getDataCriacao());
        dto.setLida(n.getLida());
        return dto;
    }
}