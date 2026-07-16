package br.com.fiap.usuarios.application.usecase.itemcardapio;

import br.com.fiap.usuarios.application.dto.itemcardapio.ItemCardapioRequestDTO;
import br.com.fiap.usuarios.application.mapper.ItemCardapioMapper;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.ItemCardapio;
import br.com.fiap.usuarios.domain.repository.ItemCardapioRepository;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Caso de uso: cadastrar um novo item de cardápio, garantindo que o restaurante exista.
 */
@Component
public class CriarItemCardapioUseCase implements UseCase {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public CriarItemCardapioUseCase(ItemCardapioRepository itemCardapioRepository, RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public void execute(Long restauranteId, ItemCardapioRequestDTO dto) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException("Restaurante não encontrado com id: " + restauranteId);
        }

        ItemCardapio itemCardapio = ItemCardapioMapper.toEntity(restauranteId, dto);
        var saved = itemCardapioRepository.save(itemCardapio);
        Assert.state(saved == 1, "Erro ao salvar item do cardápio " + dto.nome());
    }
}
