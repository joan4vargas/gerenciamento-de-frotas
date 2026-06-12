// pattern/observer/NotificacaoObserver.java
package gerenciamento.frotas.TrabFinal.pattern.observer;

import gerenciamento.frotas.TrabFinal.model.entity.Notificacao;
import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import gerenciamento.frotas.TrabFinal.repository.NotificacaoRepository;
import gerenciamento.frotas.TrabFinal.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoObserver implements ViagemObserver {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoObserver(NotificacaoRepository notificacaoRepository,
                               UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onViagemCriada(Viagem viagem) {
        String mensagem = String.format(
                "Nova viagem criada! Veículo: %s | Motorista: %s | Rota: %s → %s",
                viagem.getVeiculo().getPlaca(),
                viagem.getMotorista().getNome(),
                viagem.getRota().getOrigem(),
                viagem.getRota().getDestino()
        );
        salvarNotificacaoParaTodos(mensagem);
    }

    @Override
    public void onViagemFinalizada(Viagem viagem) {
        String mensagem = String.format(
                "Viagem finalizada! Veículo: %s | Motorista: %s liberados.",
                viagem.getVeiculo().getPlaca(),
                viagem.getMotorista().getNome()
        );
        salvarNotificacaoParaTodos(mensagem);
    }

    private void salvarNotificacaoParaTodos(String mensagem) {
        usuarioRepository.findAll().forEach(usuario -> {
            Notificacao notificacao = new Notificacao();
            notificacao.setMensagem(mensagem);
            notificacao.setUsuario(usuario);
            notificacaoRepository.save(notificacao);
        });
    }
}