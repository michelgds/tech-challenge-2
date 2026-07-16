package br.com.fiap.usuarios.application.usecase.restaurante;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Restaurante;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: buscar um restaurante por ID.
 */
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
