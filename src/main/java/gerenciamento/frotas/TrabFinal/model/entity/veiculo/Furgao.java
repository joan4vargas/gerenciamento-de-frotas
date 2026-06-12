package gerenciamento.frotas.TrabFinal.model.entity.veiculo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "furgoes")
public class Furgao extends Veiculo {

    @NotBlank(message = "Marca obrigatória")
    private String marca;

    @NotNull(message = "Quantidade de assentos obrigatória")
    @Positive
    private Integer quantidadeAssentos;

    public Furgao() {}

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Integer getQuantidadeAssentos() { return quantidadeAssentos; }
    public void setQuantidadeAssentos(Integer quantidadeAssentos) { this.quantidadeAssentos = quantidadeAssentos; }
}