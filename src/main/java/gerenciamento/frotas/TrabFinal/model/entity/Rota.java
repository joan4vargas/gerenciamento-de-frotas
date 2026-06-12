package gerenciamento.frotas.TrabFinal.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "rotas")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Origem obrigatória")
    @Column(nullable = false)
    private String origem;

    @NotBlank(message = "Destino obrigatório")
    @Column(nullable = false)
    private String destino;

    @NotNull(message = "Distância obrigatória")
    @Positive(message = "Distância deve ser positiva")
    private Double distanciaKm;

    @NotNull(message = "Tempo estimado obrigatório")
    @Positive
    private Integer tempoEstimado;

    @OneToMany(mappedBy = "rota", fetch = FetchType.LAZY)
    private List<Viagem> viagens;

    public Rota() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
    public Integer getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(Integer tempoEstimado) { this.tempoEstimado = tempoEstimado; }
    public List<Viagem> getViagens() { return viagens; }
    public void setViagens(List<Viagem> viagens) { this.viagens = viagens; }
}