// pattern/strategy/CustoPorDistancia.java
package gerenciamento.frotas.TrabFinal.pattern.strategy;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import org.springframework.stereotype.Component;

@Component
public class CustoPorDistancia implements CustoStrategy {

    private static final double CUSTO_POR_KM = 4.50;

    @Override
    public double calcular(Viagem viagem) {
        double distancia = viagem.getRota().getDistanciaKm();
        return distancia * CUSTO_POR_KM;
    }

    @Override
    public String getNome() {
        return "POR_DISTANCIA";
    }
}