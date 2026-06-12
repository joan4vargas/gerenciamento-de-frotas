package gerenciamento.frotas.TrabFinal.dto.notificacao;

import java.time.LocalDateTime;

public class NotificacaoResponseDTO {
    private Long id;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private Boolean lida;

    public NotificacaoResponseDTO() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }
}