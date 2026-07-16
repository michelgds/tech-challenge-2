package br.com.fiap.usuarios.application.usecase.itemcardapio;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.ItemCardapio;
import br.com.fiap.usuarios.domain.repository.ItemCardapioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: buscar um item do cardápio por ID.
 */
@Component
public class BuscarItemCardapioPorIdUseCase implements UseCase {

    private final ItemCardapioRepository itemCardapioRepository;

    public BuscarItemCardapioPorIdUseCase(ItemCardapioRepository itemCardapioRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
    }

    public ItemCardapio execute(Long id) {
        return itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do cardápio não encontrado com id: " + id));
    }
}
