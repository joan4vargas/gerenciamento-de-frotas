package gerenciamento.frotas.TrabFinal.pattern.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("carto")
public class CartoGpsAdapter implements GpsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public double calcularDistancia(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificar(origem);
            double[] coordDestino = geocodificar(destino);
            if (coordOrigem != null && coordDestino != null) {
                return calcularHaversine(
                        coordOrigem[0], coordOrigem[1],
                        coordDestino[0], coordDestino[1]);
            }
        } catch (Exception e) {
            System.out.println("[CARTO] Erro: " + e.getMessage());
        }
        return Math.abs((origem + destino).hashCode() % 500) + 30.0;
    }

    @Override
    public int calcularTempoEstimado(String origem, String destino) {
        double distancia = calcularDistancia(origem, destino);
        // CARTO considera tráfego: 70 km/h médio
        return (int) Math.ceil((distancia / 70.0) * 60);
    }

    @Override
    public String obterRota(String origem, String destino) {
        double distancia = calcularDistancia(origem, destino);
        int tempo = calcularTempoEstimado(origem, destino);
        return "{\"provider\":\"CARTO\","
                + "\"origem\":\"" + origem + "\","
                + "\"destino\":\"" + destino + "\","
                + "\"distanciaKm\":" + String.format("%.1f", distancia) + ","
                + "\"tempoMinutos\":" + tempo + "}";
    }

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
                System.out.println("[CARTO] Geocodificado: " + cidade +
                        " → [" + lat + ", " + lon + "]");
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.out.println("[CARTO] Erro geocodificação: " + e.getMessage());
        }
        return null;
    }

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
}