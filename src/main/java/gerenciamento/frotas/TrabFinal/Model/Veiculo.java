package gerenciamento.frotas.TrabFinal.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import gerenciamento.frotas.TrabFinal.Model.Veiculo.statusVeiculo;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placa;
    private String descricao;
    private String tipo;
    private String chassi;
    private int ano;
    private int capacidadeKg;

    @Enumerated(EnumType.STRING)
    private statusVeiculo StatusVeiculo;
}
