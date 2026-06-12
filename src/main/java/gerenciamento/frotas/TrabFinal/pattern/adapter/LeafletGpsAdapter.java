package gerenciamento.frotas.TrabFinal.pattern.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("leaflet")
public class LeafletGpsAdapter implements GpsService {

    @Value("${ors.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public double calcularDistancia(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificar(origem);
            double[] coordDestino = geocodificar(destino);
            if (coordOrigem == null || coordDestino == null) {
                return calcularFallback(origem, destino);
            }
            return calcularRota(coordOrigem, coordDestino);
        } catch (Exception e) {
            System.out.println("[Leaflet] Erro na API, usando fallback: " + e.getMessage());
            return calcularFallback(origem, destino);
        }
    }

    @Override
    public int calcularTempoEstimado(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificar(origem);
            double[] coordDestino = geocodificar(destino);
            if (coordOrigem == null || coordDestino == null) {
                return (int) Math.ceil((calcularFallback(origem, destino) / 80.0) * 60);
            }
            return calcularTempo(coordOrigem, coordDestino);
        } catch (Exception e) {
            return (int) Math.ceil((calcularFallback(origem, destino) / 80.0) * 60);
        }
    }

    @Override
    public String obterRota(String origem, String destino) {
        double distancia = calcularDistancia(origem, destino);
        int tempo = calcularTempoEstimado(origem, destino);
        return "{\"provider\":\"Leaflet\","
                + "\"origem\":\"" + origem + "\","
                + "\"destino\":\"" + destino + "\","
                + "\"distanciaKm\":" + String.format("%.1f", distancia) + ","
                + "\"tempoMinutos\":" + tempo + "}";
    }

    // Geocodifica cidade → coordenadas usando Nominatim (OpenStreetMap, gratuito)
    private double[] geocodificar(String cidade) {
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
                System.out.println("[Leaflet] Geocodificado: " + cidade +
                        " → [" + lat + ", " + lon + "]");
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.out.println("[Leaflet] Erro geocodificação: " + e.getMessage());
        }
        return null;
    }

    // Calcula rota real usando OpenRouteService
    private double calcularRota(double[] origem, double[] destino) {
        try {
            String url = "https://api.openrouteservice.org/v2/directions/driving-car"
                    + "?api_key=" + apiKey
                    + "&start=" + origem[1] + "," + origem[0]
                    + "&end=" + destino[1] + "," + destino[0];

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            JsonNode root = mapper.readTree(response.getBody());
            double distanciaMetros = root
                    .path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("distance").asDouble();

            return Math.round((distanciaMetros / 1000.0) * 10.0) / 10.0;
        } catch (Exception e) {
            System.out.println("[Leaflet] Erro rota ORS: " + e.getMessage());
            return calcularHaversine(origem[0], origem[1], destino[0], destino[1]);
        }
    }

    private int calcularTempo(double[] origem, double[] destino) {
        try {
            String url = "https://api.openrouteservice.org/v2/directions/driving-car"
                    + "?api_key=" + apiKey
                    + "&start=" + origem[1] + "," + origem[0]
                    + "&end=" + destino[1] + "," + destino[0];

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            JsonNode root = mapper.readTree(response.getBody());
            double duracaoSegundos = root
                    .path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("duration").asDouble();

            return (int) Math.ceil(duracaoSegundos / 60.0);
        } catch (Exception e) {
            double distancia = calcularHaversine(
                    origem[0], origem[1], destino[0], destino[1]);
            return (int) Math.ceil((distancia / 80.0) * 60);
        }
    }

    // Fórmula Haversine como fallback
    private double calcularHaversine(double lat1, double lon1,
                                     double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 1.3;
    }

    private double calcularFallback(String origem, String destino) {
        return Math.abs((origem + destino).hashCode() % 500) + 30.0;
    }
}