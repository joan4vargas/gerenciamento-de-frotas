package gerenciamento.frotas.TrabFinal.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "motoristas")
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "CPF obrigatório")
    @Column(unique = true, nullable = false, length = 14)
    private String cpf;

    @NotBlank(message = "CNH obrigatória")
    @Column(unique = true, nullable = false)
    private String cnh;

    @NotBlank(message = "Categoria da CNH obrigatória")
    private String categoriaCnh;

    @NotBlank(message = "Telefone obrigatório")
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMotorista status = StatusMotorista.DISPONIVEL;

    @OneToMany(mappedBy = "motorista", fetch = FetchType.LAZY)
    private List<Viagem> viagens;

    public Motorista() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }
    public String getCategoriaCnh() { return categoriaCnh; }
    public void setCategoriaCnh(String categoriaCnh) { this.categoriaCnh = categoriaCnh; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public StatusMotorista getStatus() { return status; }
    public void setStatus(StatusMotorista status) { this.status = status; }
    public List<Viagem> getViagens() { return viagens; }
    public void setViagens(List<Viagem> viagens) { this.viagens = viagens; }
}