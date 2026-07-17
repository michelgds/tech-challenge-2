package br.com.fiap.techchallenge.application.usecase.restaurante;

import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExcluirRestauranteUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;
    private final ItemCardapioRepository itemCardapioRepository;

    public ExcluirRestauranteUseCase(RestauranteRepository restauranteRepository,
                                      ItemCardapioRepository itemCardapioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.itemCardapioRepository = itemCardapioRepository;
    }

    @Transactional
    public void execute(Long id) {
        restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));
        itemCardapioRepository.deleteByRestauranteId(id);
        restauranteRepository.delete(id);
    }
}
