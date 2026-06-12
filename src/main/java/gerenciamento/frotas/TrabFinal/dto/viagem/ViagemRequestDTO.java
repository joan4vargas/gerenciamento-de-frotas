package gerenciamento.frotas.TrabFinal.dto.viagem;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ViagemRequestDTO {

    @NotNull(message = "Veículo obrigatório")
    private Long veiculoId;
    @NotNull(message = "Motorista obrigatório")
    private Long motoristaId;
    @NotNull(message = "Rota obrigatória")
    private Long rotaId;
    @NotNull(message = "Data de partida obrigatória")
    private LocalDateTime dataPartida;
    @NotNull(message = "Peso da carga obrigatório") @Positive
    private Double pesoCarga;
    @NotNull(message = "Valor da carga obrigatório") @PositiveOrZero
    private Double valorCarga;

    public ViagemRequestDTO() {}
    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }
    public Long getMotoristaId() { return motoristaId; }
    public void setMotoristaId(Long motoristaId) { this.motoristaId = motoristaId; }
    public Long getRotaId() { return rotaId; }
    public void setRotaId(Long rotaId) { this.rotaId = rotaId; }
    public LocalDateTime getDataPartida() { return dataPartida; }
    public void setDataPartida(LocalDateTime dataPartida) { this.dataPartida = dataPartida; }
    public Double getPesoCarga() { return pesoCarga; }
    public void setPesoCarga(Double pesoCarga) { this.pesoCarga = pesoCarga; }
    public Double getValorCarga() { return valorCarga; }
    public void setValorCarga(Double valorCarga) { this.valorCarga = valorCarga; }
}