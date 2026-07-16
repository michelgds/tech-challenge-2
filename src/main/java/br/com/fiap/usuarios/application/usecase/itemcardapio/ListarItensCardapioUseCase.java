package br.com.fiap.usuarios.application.usecase.itemcardapio;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.ItemCardapio;
import br.com.fiap.usuarios.domain.repository.ItemCardapioRepository;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso: listar itens do cardápio de um restaurante, com paginação.
 */
@Component
public class ListarItensCardapioUseCase implements UseCase {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public ListarItensCardapioUseCase(ItemCardapioRepository itemCardapioRepository, RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public List<ItemCardapio> execute(Long restauranteId, int page, int size) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException("Restaurante não encontrado com id: " + restauranteId);
        }
        int offset = (page - 1) * size;
        return itemCardapioRepository.findByRestauranteId(restauranteId, size, offset);
    }
}
