package gerenciamento.frotas.TrabFinal.dto.viagem;

import gerenciamento.frotas.TrabFinal.dto.motorista.MotoristaResponseDTO;
import gerenciamento.frotas.TrabFinal.dto.rota.RotaResponseDTO;
import gerenciamento.frotas.TrabFinal.dto.veiculo.VeiculoResponseDTO;
import java.time.LocalDateTime;

public class ViagemResponseDTO {
    private Long id;
    private LocalDateTime dataPartida;
    private LocalDateTime previsaoChegada;
    private Double pesoCarga;
    private Double valorCarga;
    private Boolean statusViagem;
    private Double custoCalculado;
    private VeiculoResponseDTO veiculo;
    private MotoristaResponseDTO motorista;
    private RotaResponseDTO rota;

    public ViagemResponseDTO() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataPartida() { return dataPartida; }
    public void setDataPartida(LocalDateTime dataPartida) { this.dataPartida = dataPartida; }
    public LocalDateTime getPrevisaoChegada() { return previsaoChegada; }
    public void setPrevisaoChegada(LocalDateTime previsaoChegada) { this.previsaoChegada = previsaoChegada; }
    public Double getPesoCarga() { return pesoCarga; }
    public void setPesoCarga(Double pesoCarga) { this.pesoCarga = pesoCarga; }
    public Double getValorCarga() { return valorCarga; }
    public void setValorCarga(Double valorCarga) { this.valorCarga = valorCarga; }
    public Boolean getStatusViagem() { return statusViagem; }
    public void setStatusViagem(Boolean statusViagem) { this.statusViagem = statusViagem; }
    public Double getCustoCalculado() { return custoCalculado; }
    public void setCustoCalculado(Double custoCalculado) { this.custoCalculado = custoCalculado; }
    public VeiculoResponseDTO getVeiculo() { return veiculo; }
    public void setVeiculo(VeiculoResponseDTO veiculo) { this.veiculo = veiculo; }
    public MotoristaResponseDTO getMotorista() { return motorista; }
    public void setMotorista(MotoristaResponseDTO motorista) { this.motorista = motorista; }
    public RotaResponseDTO getRota() { return rota; }
    public void setRota(RotaResponseDTO rota) { this.rota = rota; }
}