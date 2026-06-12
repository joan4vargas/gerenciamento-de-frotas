// pattern/observer/ViagemObserver.java
package gerenciamento.frotas.TrabFinal.pattern.observer;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;

public interface ViagemObserver {
    void onViagemCriada(Viagem viagem);
    void onViagemFinalizada(Viagem viagem);
}