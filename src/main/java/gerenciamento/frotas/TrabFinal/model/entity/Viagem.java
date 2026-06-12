package gerenciamento.frotas.TrabFinal.model.entity;

import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Veiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "viagens")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data de partida obrigatória")
    private LocalDateTime dataPartida;

    private LocalDateTime previsaoChegada;

    @NotNull(message = "Peso da carga obrigatório")
    @Positive(message = "Peso deve ser positivo")
    private Double pesoCarga;

    @NotNull(message = "Valor da carga obrigatório")
    @PositiveOrZero
    private Double valorCarga;

    @Column(nullable = false)
    private Boolean statusViagem = false;

    private Double custoCalculado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_id", nullable = false)
    private Rota rota;

    public Viagem() {}

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
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public Motorista getMotorista() { return motorista; }
    public void setMotorista(Motorista motorista) { this.motorista = motorista; }
    public Rota getRota() { return rota; }
    public void setRota(Rota rota) { this.rota = rota; }
}