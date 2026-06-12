package gerenciamento.frotas.TrabFinal.dto.usuario;

import jakarta.validation.constraints.*;

public class LoginRequestDTO {

    @NotBlank(message = "Email obrigatório") @Email
    private String email;
    @NotBlank(message = "Senha obrigatória")
    private String senha;

    public LoginRequestDTO() {}
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}