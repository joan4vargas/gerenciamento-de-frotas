package gerenciamento.frotas.TrabFinal.model.entity.veiculo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "caminhoes")
public class Caminhao extends Veiculo {

    @NotBlank(message = "Tipo do caminhão obrigatório")
    private String tipoCaminhao;

    @NotBlank(message = "Marca obrigatória")
    private String marca;

    @NotNull(message = "Número de eixos obrigatório")
    @Min(value = 2)
    private Integer numeroEixos;

    public Caminhao() {}

    public String getTipoCaminhao() { return tipoCaminhao; }
    public void setTipoCaminhao(String tipoCaminhao) { this.tipoCaminhao = tipoCaminhao; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Integer getNumeroEixos() { return numeroEixos; }
    public void setNumeroEixos(Integer numeroEixos) { this.numeroEixos = numeroEixos; }
}