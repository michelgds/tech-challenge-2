package br.com.fiap.usuarios.application.usecase.itemcardapio;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.ItemCardapio;
import br.com.fiap.usuarios.domain.repository.ItemCardapioRepository;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: excluir um item de cardápio, validando que ele pertença ao
 * restaurante informado na URL.
 */
@Component
public class ExcluirItemCardapioUseCase implements UseCase {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public ExcluirItemCardapioUseCase(ItemCardapioRepository itemCardapioRepository, RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public void execute(Long restauranteId, Long id) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException("Restaurante não encontrado com id: " + restauranteId);
        }

        ItemCardapio existente = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do cardápio não encontrado com id: " + id));

        if (!existente.getRestauranteId().equals(restauranteId)) {
            throw new ResourceNotFoundException("Item do cardápio não encontrado para o restaurante " + restauranteId);
        }

        itemCardapioRepository.delete(id);
    }
}
