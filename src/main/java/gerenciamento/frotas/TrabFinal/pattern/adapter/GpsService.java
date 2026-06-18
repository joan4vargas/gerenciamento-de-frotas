package gerenciamento.frotas.TrabFinal.pattern.adapter;

public interface GpsService {

    Double calcularDistancia(String origem, String destino);

    Integer calcularTempoEstimado(String origem, String destino);

    String obterRota(String origem, String destino);

    String getNome();
}