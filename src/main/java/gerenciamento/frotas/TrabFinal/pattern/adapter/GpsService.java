// pattern/adapter/GpsService.java
package gerenciamento.frotas.TrabFinal.pattern.adapter;

public interface GpsService {
    double calcularDistancia(String origem, String destino);
    int calcularTempoEstimado(String origem, String destino);
    String obterRota(String origem, String destino);
}