package gerenciamento.frotas.TrabFinal.dto.rota;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RotaResponseDTO {
    private Long id;
    private String origem;
    private String destino;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "%.3f")
    private Double distanciaKm;
    private Integer tempoEstimado;

    public RotaResponseDTO() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) {
        // Arredonda para 3 casas decimais
        this.distanciaKm = distanciaKm != null ?
                Math.round(distanciaKm * 1000.0) / 1000.0 : null;
    }
    public Integer getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(Integer tempoEstimado) { this.tempoEstimado = tempoEstimado; }
}