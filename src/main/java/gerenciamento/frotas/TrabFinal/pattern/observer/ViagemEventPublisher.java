// pattern/observer/ViagemEventPublisher.java
package gerenciamento.frotas.TrabFinal.pattern.observer;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ViagemEventPublisher {

    private final List<ViagemObserver> observers;

    public ViagemEventPublisher(List<ViagemObserver> observers) {
        this.observers = observers;
    }

    public void publicarViagemCriada(Viagem viagem) {
        observers.forEach(o -> o.onViagemCriada(viagem));
    }

    public void publicarViagemFinalizada(Viagem viagem) {
        observers.forEach(o -> o.onViagemFinalizada(viagem));
    }
}