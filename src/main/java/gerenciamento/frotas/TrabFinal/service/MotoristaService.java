package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.motorista.MotoristaRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.motorista.MotoristaResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.BusinessException;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.Motorista;
import gerenciamento.frotas.TrabFinal.model.entity.StatusMotorista;
import gerenciamento.frotas.TrabFinal.repository.MotoristaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MotoristaService {

    private final MotoristaRepository motoristaRepository;

    public MotoristaService(MotoristaRepository motoristaRepository) {
        this.motoristaRepository = motoristaRepository;
    }

    @Transactional
    public MotoristaResponseDTO criar(MotoristaRequestDTO dto) {
        if (motoristaRepository.existsByCpf(dto.getCpf()))
            throw new BusinessException("CPF já cadastrado: " + dto.getCpf());
        if (motoristaRepository.existsByCnh(dto.getCnh()))
            throw new BusinessException("CNH já cadastrada: " + dto.getCnh());

        Motorista m = new Motorista();
        m.setNome(dto.getNome());
        m.setCpf(dto.getCpf());
        m.setCnh(dto.getCnh());
        m.setCategoriaCnh(dto.getCategoriaCnh());
        m.setTelefone(dto.getTelefone());
        return toDTO(motoristaRepository.save(m));
    }

    public Page<MotoristaResponseDTO> listar(Pageable pageable) {
        return motoristaRepository.findAll(pageable).map(this::toDTO);
    }

    public MotoristaResponseDTO buscarPorId(Long id) {
        return toDTO(motoristaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista", id)));
    }

    @Transactional
    public MotoristaResponseDTO atualizar(Long id, MotoristaRequestDTO dto) {
        Motorista m = motoristaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista", id));
        m.setNome(dto.getNome());
        m.setCategoriaCnh(dto.getCategoriaCnh());
        m.setTelefone(dto.getTelefone());
        return toDTO(motoristaRepository.save(m));
    }

    @Transactional
    public void deletar(Long id) {
        Motorista m = motoristaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista", id));
        if (m.getStatus() == StatusMotorista.EM_VIAGEM)
            throw new BusinessException("Não é possível excluir motorista em viagem.");
        motoristaRepository.delete(m);
    }

    @Transactional
    public MotoristaResponseDTO atualizarStatus(Long id, String status) {
        Motorista m = motoristaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista", id));
        m.setStatus(StatusMotorista.valueOf(status.toUpperCase()));
        return toDTO(motoristaRepository.save(m));
    }

    public MotoristaResponseDTO toDTO(Motorista m) {
        MotoristaResponseDTO dto = new MotoristaResponseDTO();
        dto.setId(m.getId());
        dto.setNome(m.getNome());
        dto.setCpf(m.getCpf());
        dto.setCnh(m.getCnh());
        dto.setCategoriaCnh(m.getCategoriaCnh());
        dto.setTelefone(m.getTelefone());
        dto.setStatus(m.getStatus());
        return dto;
    }
}