// pattern/factory/VeiculoFactory.java
package gerenciamento.frotas.TrabFinal.pattern.factory;

import gerenciamento.frotas.TrabFinal.dto.veiculo.VeiculoRequestDTO;
import gerenciamento.frotas.TrabFinal.exception.BusinessException;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.*;
import org.springframework.stereotype.Component;

@Component
public class VeiculoFactory {

    public Veiculo criar(VeiculoRequestDTO dto) {
        if (dto.getTipo() == null) {
            throw new BusinessException("Tipo de veículo obrigatório");
        }

        switch (dto.getTipo().toUpperCase()) {
            case "CAMINHAO" -> {
                Caminhao c = new Caminhao();
                preencherBase(c, dto);
                c.setMarca(dto.getMarca());
                c.setTipoCaminhao(dto.getTipoCaminhao());
                c.setNumeroEixos(dto.getNumeroEixos());
                return c;
            }
            case "FURGAO" -> {
                Furgao f = new Furgao();
                preencherBase(f, dto);
                f.setMarca(dto.getMarca());
                f.setQuantidadeAssentos(dto.getQuantidadeAssentos());
                return f;
            }
            case "REBOQUE" -> {
                Reboque r = new Reboque();
                preencherBase(r, dto);
                r.setTipoReboque(dto.getTipoReboque());
                r.setNumeroEixos(dto.getNumeroEixos());
                return r;
            }
            default -> throw new BusinessException(
                    "Tipo de veículo inválido: " + dto.getTipo() +
                            ". Use: CAMINHAO, FURGAO ou REBOQUE"
            );
        }
    }

    private void preencherBase(Veiculo v, VeiculoRequestDTO dto) {
        v.setPlaca(dto.getPlaca());
        v.setDescricao(dto.getDescricao());
        v.setTipo(dto.getTipo().toUpperCase());
        v.setChassi(dto.getChassi());
        v.setAno(dto.getAno());
        v.setCapacidadeKg(dto.getCapacidadeKg());
        v.setStatus(StatusVeiculo.DISPONIVEL);
    }
}