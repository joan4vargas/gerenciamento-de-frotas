package gerenciamento.frotas.TrabFinal.dto.motorista;

import jakarta.validation.constraints.*;

public class MotoristaRequestDTO {

    @NotBlank(message = "Nome obrigatório")
    private String nome;
    @NotBlank(message = "CPF obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF inválido")
    private String cpf;
    @NotBlank(message = "CNH obrigatória")
    private String cnh;
    @NotBlank(message = "Categoria da CNH obrigatória")
    private String categoriaCnh;
    @NotBlank(message = "Telefone obrigatório")
    private String telefone;

    public MotoristaRequestDTO() {}
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
}