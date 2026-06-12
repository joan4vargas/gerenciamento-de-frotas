// pattern/strategy/CustoPorPeso.java
package gerenciamento.frotas.TrabFinal.pattern.strategy;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import org.springframework.stereotype.Component;

@Component
public class CustoPorPeso implements CustoStrategy {

    private static final double CUSTO_POR_KG_KM = 0.02;

    @Override
    public double calcular(Viagem viagem) {
        double distancia = viagem.getRota().getDistanciaKm();
        double peso = viagem.getPesoCarga();
        return distancia * peso * CUSTO_POR_KG_KM;
    }

    @Override
    public String getNome() {
        return "POR_PESO";
    }
}