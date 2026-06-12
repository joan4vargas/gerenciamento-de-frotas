package gerenciamento.frotas.TrabFinal.model.entity.veiculo;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "veiculos")
public abstract class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Placa obrigatória")
    @Column(unique = true, nullable = false, length = 10)
    private String placa;

    @NotBlank(message = "Descrição obrigatória")
    @Column(nullable = false)
    private String descricao;

    @NotBlank(message = "Tipo obrigatório")
    @Column(nullable = false)
    private String tipo;

    @NotBlank(message = "Chassi obrigatório")
    @Column(unique = true, nullable = false)
    private String chassi;

    @NotNull(message = "Ano obrigatório")
    @Min(value = 1990, message = "Ano inválido")
    private Integer ano;

    @NotNull(message = "Capacidade obrigatória")
    @Positive(message = "Capacidade deve ser positiva")
    private Integer capacidadeKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVeiculo status = StatusVeiculo.DISPONIVEL;

    @OneToMany(mappedBy = "veiculo", fetch = FetchType.LAZY)
    private List<Viagem> viagens;

    public Veiculo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getChassi() { return chassi; }
    public void setChassi(String chassi) { this.chassi = chassi; }
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    public Integer getCapacidadeKg() { return capacidadeKg; }
    public void setCapacidadeKg(Integer capacidadeKg) { this.capacidadeKg = capacidadeKg; }
    public StatusVeiculo getStatus() { return status; }
    public void setStatus(StatusVeiculo status) { this.status = status; }
    public List<Viagem> getViagens() { return viagens; }
    public void setViagens(List<Viagem> viagens) { this.viagens = viagens; }
}