package br.com.fiap.techchallenge.application.usecase.itemcardapio;

import br.com.fiap.techchallenge.application.dto.itemcardapio.ItemCardapioRequestDTO;
import br.com.fiap.techchallenge.application.mapper.ItemCardapioMapper;
import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.ItemCardapio;
import br.com.fiap.techchallenge.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

@Component
public class AtualizarItemCardapioUseCase implements UseCase {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public AtualizarItemCardapioUseCase(ItemCardapioRepository itemCardapioRepository, RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public void execute(Long restauranteId, Long id, ItemCardapioRequestDTO dto) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException("Restaurante não encontrado com id: " + restauranteId);
        }

        ItemCardapio existente = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do cardápio não encontrado com id: " + id));

        if (!existente.getRestauranteId().equals(restauranteId)) {
            throw new ResourceNotFoundException("Item do cardápio não encontrado para o restaurante " + restauranteId);
        }

        ItemCardapio itemCardapio = ItemCardapioMapper.toEntity(restauranteId, dto);
        var updated = itemCardapioRepository.update(itemCardapio, id);
        if (updated == 0) {
            throw new ResourceNotFoundException("Item do cardápio não encontrado com id: " + id);
        }
    }
}
