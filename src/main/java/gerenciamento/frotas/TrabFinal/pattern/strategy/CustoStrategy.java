// pattern/strategy/CustoStrategy.java
package gerenciamento.frotas.TrabFinal.pattern.strategy;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;

public interface CustoStrategy {
    double calcular(Viagem viagem);
    String getNome();
}