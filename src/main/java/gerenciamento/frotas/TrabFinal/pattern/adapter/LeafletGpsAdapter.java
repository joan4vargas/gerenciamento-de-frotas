package gerenciamento.frotas.TrabFinal.pattern.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Component("leaflet")
public class LeafletGpsAdapter implements GpsService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String OSRM_API = "https://router.project-osrm.org/route/v1/driving/";
    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search";

    @Override
    public Double calcularDistancia(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificarNominatim(origem);
            double[] coordDestino = geocodificarNominatim(destino);
            return calcularRotaOSRM(coordOrigem, coordDestino);
        } catch (Exception e) {
            System.err.println("Erro ao calcular distância: " + e.getMessage());
            return 50.0;
        }
    }

    @Override
    public Integer calcularTempoEstimado(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificarNominatim(origem);
            double[] coordDestino = geocodificarNominatim(destino);
            return calcularTempoOSRM(coordOrigem, coordDestino);
        } catch (Exception e) {
            Double distancia = calcularDistancia(origem, destino);
            return (int) (distancia / 80 * 60);
        }
    }

    @Override
    public String obterRota(String origem, String destino) {
        try {
            double[] coordOrigem = geocodificarNominatim(origem);
            double[] coordDestino = geocodificarNominatim(destino);
            double distancia = calcularRotaOSRM(coordOrigem, coordDestino);
            Integer tempo = calcularTempoOSRM(coordOrigem, coordDestino);
            return "{ \"provider\": \"OSRM\", \"distanciaKm\": " + distancia + ", \"tempoMinutos\": " + tempo + " }";
        } catch (Exception e) {
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }

    @Override
    public String getNome() {
        return "OSRM+Nominatim";
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

    private Double calcularRotaOSRM(double[] coordOrigem, double[] coordDestino) throws Exception {
        String url = OSRM_API + coordOrigem[1] + "," + coordOrigem[0] + ";" + coordDestino[1] + "," + coordDestino[0];
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject json = new JSONObject(response.getBody());

        if (json.has("routes") && json.getJSONArray("routes").length() > 0) {
            double distanciaMetros = json.getJSONArray("routes").getJSONObject(0).getDouble("distance");
            return distanciaMetros / 1000.0;
        } else {
            throw new Exception("OSRM sem rota disponível");
        }
    }

    private Integer calcularTempoOSRM(double[] coordOrigem, double[] coordDestino) throws Exception {
        String url = OSRM_API + coordOrigem[1] + "," + coordOrigem[0] + ";" + coordDestino[1] + "," + coordDestino[0];
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject json = new JSONObject(response.getBody());

        if (json.has("routes") && json.getJSONArray("routes").length() > 0) {
            double tempoSegundos = json.getJSONArray("routes").getJSONObject(0).getDouble("duration");
            return (int) (tempoSegundos / 60);
        } else {
            throw new Exception("OSRM sem rota disponível");
        }
    }
}