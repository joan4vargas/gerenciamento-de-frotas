package gerenciamento.frotas.TrabFinal.pattern.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Component("carto")
public class CartoGpsAdapter implements GpsService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search";

    @Override
    public Double calcularDistancia(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificarNominatim(origem);
            double[] coordDestino = geocodificarNominatim(destino);
            return calcularHaversine(coordOrigem[0], coordOrigem[1],
                    coordDestino[0], coordDestino[1]) * 1.3;
        } catch (Exception e) {
            System.err.println("Erro ao calcular distância: " + e.getMessage());
            return 50.0;
        }
    }

    @Override
    public Integer calcularTempoEstimado(String origem, String destino) {
        try {
            Double distancia = calcularDistancia(origem, destino);
            return (int) (distancia / 70 * 60); // 70 km/h (mais conservador que Leaflet)
        } catch (Exception e) {
            return 60; // 1 hora como padrão
        }
    }

    @Override
    public String obterRota(String origem, String destino) {
        try {
            Double distancia = calcularDistancia(origem, destino);
            Integer tempo = calcularTempoEstimado(origem, destino);
            return "{ \"provider\": \"CARTO+Haversine\", \"distanciaKm\": " + distancia + ", \"tempoMinutos\": " + tempo + " }";
        } catch (Exception e) {
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }

    @Override
    public String getNome() {
        return "CARTO+Haversine";
    }

    // ═══ MÉTODOS PRIVADOS ═══

    private double[] geocodificarNominatim(String cidade) throws Exception {
        String url = NOMINATIM_API + "?q=" + cidade.replace(" ", "+") + "&format=json&limit=1";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONArray results = new JSONArray(response.getBody());

        if (results.length() == 0) {
            throw new Exception("Cidade não encontrada: " + cidade);
        }

        JSONObject local = results.getJSONObject(0);
        double lat = local.getDouble("lat");
        double lon = local.getDouble("lon");

        return new double[]{lat, lon};
    }

    private Double calcularHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int RAIO_TERRA_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c;
    }
}