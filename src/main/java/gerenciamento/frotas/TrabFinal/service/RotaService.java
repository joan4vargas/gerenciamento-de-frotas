package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.rota.RotaRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.rota.RotaResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.Rota;
import gerenciamento.frotas.TrabFinal.pattern.adapter.GpsService;
import gerenciamento.frotas.TrabFinal.repository.RotaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class RotaService {

    private final RotaRepository rotaRepository;
    private final GpsService gpsService;

    public RotaService(RotaRepository rotaRepository,
                       @Qualifier("leaflet") GpsService gpsService) {
        this.rotaRepository = rotaRepository;
        this.gpsService = gpsService;
    }

    @Transactional
    public RotaResponseDTO criar(RotaRequestDTO dto) {
        Rota rota = new Rota();
        rota.setOrigem(dto.getOrigem());
        rota.setDestino(dto.getDestino());
        rota.setDistanciaKm(dto.getDistanciaKm());
        rota.setTempoEstimado(dto.getTempoEstimado());
        return toDTO(rotaRepository.save(rota));
    }

    @Transactional
    public RotaResponseDTO criarComGps(String origem, String destino) {
        double distancia = gpsService.calcularDistancia(origem, destino);
        int tempo = gpsService.calcularTempoEstimado(origem, destino);

        Rota rota = new Rota();
        rota.setOrigem(origem);
        rota.setDestino(destino);
        rota.setDistanciaKm(distancia);
        rota.setTempoEstimado(tempo);
        return toDTO(rotaRepository.save(rota));
    }

    // Retorna Map ao invés de String para serialização correta
    public Map<String, Object> obterRotaGps(String origem, String destino) {
        double distancia = gpsService.calcularDistancia(origem, destino);
        int tempo = gpsService.calcularTempoEstimado(origem, destino);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("provider", "Leaflet");
        resultado.put("origem", origem);
        resultado.put("destino", destino);
        resultado.put("distanciaKm", Math.round(distancia * 10.0) / 10.0);
        resultado.put("tempoMinutos", tempo);
        return resultado;
    }

    public Page<RotaResponseDTO> listar(Pageable pageable) {
        return rotaRepository.findAll(pageable).map(this::toDTO);
    }

    public RotaResponseDTO buscarPorId(Long id) {
        return toDTO(rotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rota", id)));
    }

    @Transactional
    public RotaResponseDTO atualizar(Long id, RotaRequestDTO dto) {
        Rota rota = rotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rota", id));
        rota.setOrigem(dto.getOrigem());
        rota.setDestino(dto.getDestino());
        rota.setDistanciaKm(dto.getDistanciaKm());
        rota.setTempoEstimado(dto.getTempoEstimado());
        return toDTO(rotaRepository.save(rota));
    }

    @Transactional
    public void deletar(Long id) {
        Rota rota = rotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rota", id));
        rotaRepository.delete(rota);
    }

    public RotaResponseDTO toDTO(Rota r) {
        RotaResponseDTO dto = new RotaResponseDTO();
        dto.setId(r.getId());
        dto.setOrigem(r.getOrigem());
        dto.setDestino(r.getDestino());
        dto.setDistanciaKm(r.getDistanciaKm());
        dto.setTempoEstimado(r.getTempoEstimado());
        return dto;
    }
}