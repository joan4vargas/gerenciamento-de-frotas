package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.dto.notificacao.NotificacaoResponseDTO;
import gerenciamento.frotas.TrabFinal.service.NotificacaoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarNaoLidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacaoService.listarNaoLidas(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<NotificacaoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarNaoLidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacaoService.contarNaoLidas(usuarioId));
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/usuario/{usuarioId}/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@PathVariable Long usuarioId) {
        notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.noContent().build();
    }
}