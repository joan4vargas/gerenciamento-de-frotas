// pattern/facade/ViagemFacade.java
package gerenciamento.frotas.TrabFinal.pattern.facade;

import gerenciamento.frotas.TrabFinal.exception.BusinessException;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.*;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.StatusVeiculo;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Veiculo;
import gerenciamento.frotas.TrabFinal.pattern.observer.ViagemEventPublisher;
import gerenciamento.frotas.TrabFinal.pattern.strategy.CustoStrategy;
import gerenciamento.frotas.TrabFinal.repository.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ViagemFacade {

    private final ViagemRepository viagemRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final RotaRepository rotaRepository;
    private final ViagemEventPublisher eventPublisher;
    private final List<CustoStrategy> estrategias;

    public ViagemFacade(ViagemRepository viagemRepository,
                        VeiculoRepository veiculoRepository,
                        MotoristaRepository motoristaRepository,
                        RotaRepository rotaRepository,
                        ViagemEventPublisher eventPublisher,
                        List<CustoStrategy> estrategias) {
        this.viagemRepository = viagemRepository;
        this.veiculoRepository = veiculoRepository;
        this.motoristaRepository = motoristaRepository;
        this.rotaRepository = rotaRepository;
        this.eventPublisher = eventPublisher;
        this.estrategias = estrategias;
    }

    public Viagem iniciarViagem(Long veiculoId, Long motoristaId,
                                Long rotaId, Double pesoCarga,
                                Double valorCarga, String estrategiaCusto) {

        // 1. Buscar entidades
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", veiculoId));

        Motorista motorista = motoristaRepository.findById(motoristaId)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista", motoristaId));

        Rota rota = rotaRepository.findById(rotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rota", rotaId));

        // 2. Regras de negócio
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new BusinessException("Veículo " + veiculo.getPlaca() +
                    " não está disponível. Status atual: " + veiculo.getStatus());
        }

        if (motorista.getStatus() != StatusMotorista.DISPONIVEL) {
            throw new BusinessException("Motorista " + motorista.getNome() +
                    " não está disponível. Status atual: " + motorista.getStatus());
        }

        if (pesoCarga > veiculo.getCapacidadeKg()) {
            throw new BusinessException("Peso da carga (" + pesoCarga +
                    " kg) excede a capacidade do veículo (" + veiculo.getCapacidadeKg() + " kg)");
        }

        // 3. Criar viagem
        Viagem viagem = new Viagem();
        viagem.setVeiculo(veiculo);
        viagem.setMotorista(motorista);
        viagem.setRota(rota);
        viagem.setDataPartida(LocalDateTime.now());
        viagem.setPesoCarga(pesoCarga);
        viagem.setValorCarga(valorCarga);
        viagem.setStatusViagem(false);

        // 4. Calcular custo via Strategy
        CustoStrategy strategy = estrategias.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(estrategiaCusto))
                .findFirst()
                .orElse(estrategias.get(0));

        viagem.setCustoCalculado(strategy.calcular(viagem));

        // 5. Calcular previsão de chegada
        viagem.setPrevisaoChegada(
                LocalDateTime.now().plusMinutes(rota.getTempoEstimado())
        );

        // 6. Atualizar status
        veiculo.setStatus(StatusVeiculo.EM_VIAGEM);
        motorista.setStatus(StatusMotorista.EM_VIAGEM);
        veiculoRepository.save(veiculo);
        motoristaRepository.save(motorista);

        Viagem viagemSalva = viagemRepository.save(viagem);

        // 7. Disparar evento Observer
        eventPublisher.publicarViagemCriada(viagemSalva);

        return viagemSalva;
    }

    public Viagem finalizarViagem(Long viagemId) {

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem", viagemId));

        if (viagem.getStatusViagem()) {
            throw new BusinessException("Viagem já foi finalizada.");
        }

        // Finalizar e liberar
        viagem.setStatusViagem(true);
        viagem.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL);
        viagem.getMotorista().setStatus(StatusMotorista.DISPONIVEL);

        veiculoRepository.save(viagem.getVeiculo());
        motoristaRepository.save(viagem.getMotorista());
        Viagem viagemFinalizada = viagemRepository.save(viagem);

        // Disparar evento Observer
        eventPublisher.publicarViagemFinalizada(viagemFinalizada);

        return viagemFinalizada;
    }
}