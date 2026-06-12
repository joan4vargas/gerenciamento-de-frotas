package gerenciamento.frotas.TrabFinal.dto.veiculo;

import gerenciamento.frotas.TrabFinal.model.entity.veiculo.StatusVeiculo;

public class VeiculoResponseDTO {
    private Long id;
    private String placa;
    private String descricao;
    private String tipo;
    private String chassi;
    private Integer ano;
    private Integer capacidadeKg;
    private StatusVeiculo status;
    private String marca;
    private String tipoCaminhao;
    private Integer numeroEixos;
    private Integer quantidadeAssentos;
    private String tipoReboque;

    public VeiculoResponseDTO() {}
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