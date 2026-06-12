package gerenciamento.frotas.TrabFinal.dto.veiculo;

import jakarta.validation.constraints.*;

public class VeiculoRequestDTO {

    @NotBlank(message = "Placa obrigatória")
    private String placa;
    @NotBlank(message = "Descrição obrigatória")
    private String descricao;
    @NotBlank(message = "Tipo obrigatório")
    private String tipo;
    @NotBlank(message = "Chassi obrigatório")
    private String chassi;
    @NotNull(message = "Ano obrigatório") @Min(value = 1990)
    private Integer ano;
    @NotNull(message = "Capacidade obrigatória") @Positive
    private Integer capacidadeKg;
    private String marca;
    private String tipoCaminhao;
    private Integer numeroEixos;
    private Integer quantidadeAssentos;
    private String tipoReboque;

    public VeiculoRequestDTO() {}
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
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getTipoCaminhao() { return tipoCaminhao; }
    public void setTipoCaminhao(String tipoCaminhao) { this.tipoCaminhao = tipoCaminhao; }
    public Integer getNumeroEixos() { return numeroEixos; }
    public void setNumeroEixos(Integer numeroEixos) { this.numeroEixos = numeroEixos; }
    public Integer getQuantidadeAssentos() { return quantidadeAssentos; }
    public void setQuantidadeAssentos(Integer quantidadeAssentos) { this.quantidadeAssentos = quantidadeAssentos; }
    public String getTipoReboque() { return tipoReboque; }
    public void setTipoReboque(String tipoReboque) { this.tipoReboque = tipoReboque; }
}