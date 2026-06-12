package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.veiculo.VeiculoRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.veiculo.VeiculoResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.BusinessException;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.*;
import gerenciamento.frotas.TrabFinal.pattern.factory.VeiculoFactory;
import gerenciamento.frotas.TrabFinal.repository.VeiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoFactory veiculoFactory;

    public VeiculoService(VeiculoRepository veiculoRepository, VeiculoFactory veiculoFactory) {
        this.veiculoRepository = veiculoRepository;
        this.veiculoFactory = veiculoFactory;
    }

    @Transactional
    public VeiculoResponseDTO criar(VeiculoRequestDTO dto) {
        if (veiculoRepository.existsByPlaca(dto.getPlaca()))
            throw new BusinessException("Placa já cadastrada: " + dto.getPlaca());
        if (veiculoRepository.existsByChassi(dto.getChassi()))
            throw new BusinessException("Chassi já cadastrado: " + dto.getChassi());

        Veiculo veiculo = veiculoFactory.criar(dto);
        return toDTO(veiculoRepository.save(veiculo));
    }

    public Page<VeiculoResponseDTO> listar(Pageable pageable) {
        return veiculoRepository.findAll(pageable).map(this::toDTO);
    }

    public VeiculoResponseDTO buscarPorId(Long id) {
        return toDTO(veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", id)));
    }

    @Transactional
    public VeiculoResponseDTO atualizar(Long id, VeiculoRequestDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", id));

        veiculo.setDescricao(dto.getDescricao());
        veiculo.setAno(dto.getAno());
        veiculo.setCapacidadeKg(dto.getCapacidadeKg());

        if (veiculo instanceof Caminhao c) {
            c.setMarca(dto.getMarca());
            c.setTipoCaminhao(dto.getTipoCaminhao());
            c.setNumeroEixos(dto.getNumeroEixos());
        } else if (veiculo instanceof Furgao f) {
            f.setMarca(dto.getMarca());
            f.setQuantidadeAssentos(dto.getQuantidadeAssentos());
        } else if (veiculo instanceof Reboque r) {
            r.setTipoReboque(dto.getTipoReboque());
            r.setNumeroEixos(dto.getNumeroEixos());
        }

        return toDTO(veiculoRepository.save(veiculo));
    }

    @Transactional
    public void deletar(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", id));
        if (veiculo.getStatus() == StatusVeiculo.EM_VIAGEM)
            throw new BusinessException("Não é possível excluir veículo em viagem.");
        veiculoRepository.delete(veiculo);
    }

    @Transactional
    public VeiculoResponseDTO atualizarStatus(Long id, String status) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", id));
        veiculo.setStatus(StatusVeiculo.valueOf(status.toUpperCase()));
        return toDTO(veiculoRepository.save(veiculo));
    }

    public VeiculoResponseDTO toDTO(Veiculo v) {
        VeiculoResponseDTO dto = new VeiculoResponseDTO();
        dto.setId(v.getId());
        dto.setPlaca(v.getPlaca());
        dto.setDescricao(v.getDescricao());
        dto.setTipo(v.getTipo());
        dto.setChassi(v.getChassi());
        dto.setAno(v.getAno());
        dto.setCapacidadeKg(v.getCapacidadeKg());
        dto.setStatus(v.getStatus());
        if (v instanceof Caminhao c) {
            dto.setMarca(c.getMarca());
            dto.setTipoCaminhao(c.getTipoCaminhao());
            dto.setNumeroEixos(c.getNumeroEixos());
        } else if (v instanceof Furgao f) {
            dto.setMarca(f.getMarca());
            dto.setQuantidadeAssentos(f.getQuantidadeAssentos());
        } else if (v instanceof Reboque r) {
            dto.setTipoReboque(r.getTipoReboque());
            dto.setNumeroEixos(r.getNumeroEixos());
        }
        return dto;
    }
}