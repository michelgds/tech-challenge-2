package br.com.fiap.techchallenge.application.usecase.restaurante;

import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

@Component
public class ExcluirRestauranteUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;

    public ExcluirRestauranteUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public void execute(Long id) {
        restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));
        restauranteRepository.delete(id);
    }
}
