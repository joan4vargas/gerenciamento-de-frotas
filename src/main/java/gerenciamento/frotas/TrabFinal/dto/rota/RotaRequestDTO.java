package gerenciamento.frotas.TrabFinal.dto.rota;

import jakarta.validation.constraints.*;

public class RotaRequestDTO {

    @NotBlank(message = "Origem obrigatória")
    private String origem;
    @NotBlank(message = "Destino obrigatório")
    private String destino;
    @NotNull(message = "Distância obrigatória") @Positive
    private Double distanciaKm;
    @NotNull(message = "Tempo estimado obrigatório") @Positive
    private Integer tempoEstimado;

    public RotaRequestDTO() {}
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
    public Integer getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(Integer tempoEstimado) { this.tempoEstimado = tempoEstimado; }
}