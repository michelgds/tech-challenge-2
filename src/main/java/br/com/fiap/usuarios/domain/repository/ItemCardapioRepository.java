package br.com.fiap.usuarios.domain.repository;

import br.com.fiap.usuarios.domain.model.ItemCardapio;

import java.util.List;
import java.util.Optional;

public interface ItemCardapioRepository {
    Optional<ItemCardapio> findById(Long id);
    List<ItemCardapio> findByRestauranteId(Long restauranteId, int size, int offset);
    Integer save(ItemCardapio itemCardapio);
    Integer update(ItemCardapio itemCardapio, Long id);
    Integer delete(Long id);
}
