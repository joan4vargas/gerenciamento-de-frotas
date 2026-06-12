package gerenciamento.frotas.TrabFinal.dto.usuario;

import jakarta.validation.constraints.*;

public class UsuarioRequestDTO {

    @NotBlank(message = "Nome obrigatório")
    private String nome;
    @NotBlank(message = "Email obrigatório") @Email(message = "Email inválido")
    private String email;
    @NotBlank(message = "Senha obrigatória") @Size(min = 6)
    private String senha;
    private String tipo = "ROLE_USER";

    public UsuarioRequestDTO() {}
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}