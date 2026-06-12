package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.viagem.ViagemRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.viagem.ViagemResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import gerenciamento.frotas.TrabFinal.pattern.facade.ViagemFacade;
import gerenciamento.frotas.TrabFinal.repository.ViagemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final ViagemFacade viagemFacade;
    private final VeiculoService veiculoService;
    private final MotoristaService motoristaService;
    private final RotaService rotaService;

    public ViagemService(ViagemRepository viagemRepository,
                         ViagemFacade viagemFacade,
                         VeiculoService veiculoService,
                         MotoristaService motoristaService,
                         RotaService rotaService) {
        this.viagemRepository = viagemRepository;
        this.viagemFacade = viagemFacade;
        this.veiculoService = veiculoService;
        this.motoristaService = motoristaService;
        this.rotaService = rotaService;
    }

    @Transactional
    public ViagemResponseDTO iniciar(ViagemRequestDTO dto, String estrategia) {
        Viagem viagem = viagemFacade.iniciarViagem(
                dto.getVeiculoId(), dto.getMotoristaId(), dto.getRotaId(),
                dto.getPesoCarga(), dto.getValorCarga(),
                estrategia != null ? estrategia : "POR_DISTANCIA"
        );
        return toDTO(viagem);
    }

    @Transactional
    public ViagemResponseDTO finalizar(Long id) {
        return toDTO(viagemFacade.finalizarViagem(id));
    }

    @Transactional(readOnly = true)
    public Page<ViagemResponseDTO> listar(Pageable pageable) {
        return viagemRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ViagemResponseDTO buscarPorId(Long id) {
        return toDTO(viagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem", id)));
    }

    @Transactional(readOnly = true)
    public Page<ViagemResponseDTO> listarAtivas(Pageable pageable) {
        return viagemRepository.findByStatusViagem(false, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ViagemResponseDTO> listarFinalizadas(Pageable pageable) {
        return viagemRepository.findByStatusViagem(true, pageable).map(this::toDTO);
    }

    public ViagemResponseDTO toDTO(Viagem v) {
        ViagemResponseDTO dto = new ViagemResponseDTO();
        dto.setId(v.getId());
        dto.setDataPartida(v.getDataPartida());
        dto.setPrevisaoChegada(v.getPrevisaoChegada());
        dto.setPesoCarga(v.getPesoCarga());
        dto.setValorCarga(v.getValorCarga());
        dto.setStatusViagem(v.getStatusViagem());
        dto.setCustoCalculado(v.getCustoCalculado());
        dto.setVeiculo(veiculoService.toDTO(v.getVeiculo()));
        dto.setMotorista(motoristaService.toDTO(v.getMotorista()));
        dto.setRota(rotaService.toDTO(v.getRota()));
        return dto;
    }
}