package br.com.fiap.techchallenge.application.usecase.itemcardapio;

import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.ItemCardapio;
import br.com.fiap.techchallenge.domain.repository.ItemCardapioRepository;
import org.springframework.stereotype.Component;

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
