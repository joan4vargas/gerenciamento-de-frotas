package gerenciamento.frotas.TrabFinal.dto.motorista;

import gerenciamento.frotas.TrabFinal.model.entity.StatusMotorista;

public class MotoristaResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String cnh;
    private String categoriaCnh;
    private String telefone;
    private StatusMotorista status;

    public MotoristaResponseDTO() {}
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
}