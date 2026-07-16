package br.com.fiap.usuarios.application.usecase.restaurante;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.model.Restaurante;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso: listar restaurantes, com paginação e filtro opcional por nome.
 */
@Component
public class ListarRestaurantesUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;

    public ListarRestaurantesUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public List<Restaurante> execute(int page, int size, String nome) {
        if (nome != null && !nome.isBlank()) {
            return restauranteRepository.findByNome(nome);
        }
        int offset = (page - 1) * size;
        return restauranteRepository.findAll(size, offset);
    }
}
