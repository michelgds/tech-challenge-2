package br.com.fiap.techchallenge.application.usecase.restaurante;

import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.Restaurante;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscarRestaurantePorIdUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;

    public BuscarRestaurantePorIdUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public Restaurante execute(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));
    }
}
