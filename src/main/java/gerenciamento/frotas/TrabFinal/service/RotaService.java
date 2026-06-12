package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.rota.RotaRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.rota.RotaResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.Rota;
import gerenciamento.frotas.TrabFinal.pattern.adapter.GpsService;
import gerenciamento.frotas.TrabFinal.repository.RotaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RotaService {

    private final RotaRepository rotaRepository;
    private final GpsService gpsService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${ors.api.key}")
    private String orsApiKey;

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

    public List<double[]> obterGeometriaRota(String origem, String destino) {
        try {
            System.out.println("[ORS] Iniciando: " + origem + " → " + destino);
            System.out.println("[ORS] API Key presente: " + (orsApiKey != null && !orsApiKey.isEmpty()));

            double[] coordOrigem = geocodificarNominatim(origem);
            double[] coordDestino = geocodificarNominatim(destino);

            System.out.println("[ORS] Coord Origem: " + (coordOrigem != null ? coordOrigem[0] + "," + coordOrigem[1] : "NULL"));
            System.out.println("[ORS] Coord Destino: " + (coordDestino != null ? coordDestino[0] + "," + coordDestino[1] : "NULL"));

            if (coordOrigem == null || coordDestino == null) {
                System.out.println("[ORS] Geocodificação falhou!");
                return List.of();
            }

            String url = "https://api.openrouteservice.org/v2/directions/driving-car"
                    + "?api_key=" + orsApiKey
                    + "&start=" + coordOrigem[1] + "," + coordOrigem[0]
                    + "&end=" + coordDestino[1] + "," + coordDestino[0];

            System.out.println("[ORS] URL (parcial): " + url.substring(0, Math.min(100, url.length())));

            HttpHeaders headers = new HttpHeaders();
            // ORS exige application/geo+json
            headers.set("Accept", "application/geo+json");
            headers.set("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            System.out.println("[ORS] HTTP Status: " + response.getStatusCode());

            JsonNode root = mapper.readTree(response.getBody());

            JsonNode features = root.path("features");
            if (features.isMissingNode() || features.isEmpty()) {
                System.out.println("[ORS] Sem features na resposta");
                return List.of();
            }

            // geo+json: features[0].geometry.coordinates
            JsonNode coords = features.get(0)
                    .path("geometry")
                    .path("coordinates");

            System.out.println("[ORS] Pontos recebidos: " + coords.size());

            List<double[]> pontos = new ArrayList<>();
            for (JsonNode c : coords) {
                // ORS retorna [lon, lat] → convertemos para [lat, lon] (Leaflet)
                pontos.add(new double[]{c.get(1).asDouble(), c.get(0).asDouble()});
            }

            System.out.println("[ORS] Geometria OK: " + pontos.size() + " pontos");
            return pontos;

        } catch (Exception e) {
            System.out.println("[ORS] ERRO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return List.of();
        }
    }

    private double[] geocodificarNominatim(String cidade) {
        try {
            String url = "https://nominatim.openstreetmap.org/search"
                    + "?q=" + cidade.replace(" ", "+") + ",Brasil"
                    + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "GerenciamentoFrotas/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            JsonNode root = mapper.readTree(response.getBody());
            if (root.isArray() && root.size() > 0) {
                double lat = root.get(0).get("lat").asDouble();
                double lon = root.get(0).get("lon").asDouble();
                System.out.println("[Nominatim] " + cidade + " → [" + lat + ", " + lon + "]");
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.out.println("[Nominatim] Erro: " + e.getMessage());
        }
        return null;
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