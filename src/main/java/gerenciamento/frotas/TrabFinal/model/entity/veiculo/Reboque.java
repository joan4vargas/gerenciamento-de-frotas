package gerenciamento.frotas.TrabFinal.model.entity.veiculo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "reboques")
public class Reboque extends Veiculo {

    @NotBlank(message = "Tipo do reboque obrigatório")
    private String tipoReboque;

    @NotNull(message = "Número de eixos obrigatório")
    @Min(value = 1)
    private Integer numeroEixos;

    public Reboque() {}

    public String getTipoReboque() { return tipoReboque; }
    public void setTipoReboque(String tipoReboque) { this.tipoReboque = tipoReboque; }
    public Integer getNumeroEixos() { return numeroEixos; }
    public void setNumeroEixos(Integer numeroEixos) { this.numeroEixos = numeroEixos; }
}