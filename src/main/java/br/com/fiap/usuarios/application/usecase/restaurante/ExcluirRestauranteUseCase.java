package br.com.fiap.usuarios.application.usecase.restaurante;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: excluir um restaurante existente.
 */
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
