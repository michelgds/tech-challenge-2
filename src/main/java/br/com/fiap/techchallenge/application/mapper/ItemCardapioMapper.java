package br.com.fiap.techchallenge.application.mapper;

import br.com.fiap.techchallenge.application.dto.itemcardapio.ItemCardapioRequestDTO;
import br.com.fiap.techchallenge.application.dto.itemcardapio.ItemCardapioResponseDTO;
import br.com.fiap.techchallenge.domain.model.ItemCardapio;

public final class ItemCardapioMapper {

    private ItemCardapioMapper() {
    }

    public static ItemCardapio toEntity(Long restauranteId, ItemCardapioRequestDTO dto) {
        ItemCardapio itemCardapio = new ItemCardapio();
        itemCardapio.setRestauranteId(restauranteId);
        itemCardapio.setNome(dto.nome());
        itemCardapio.setDescricao(dto.descricao());
        itemCardapio.setPreco(dto.preco());
        itemCardapio.setDisponivelSomenteLocal(dto.disponivelSomenteLocal());
        itemCardapio.setFotoPath(dto.fotoPath());
        return itemCardapio;
    }

    public static ItemCardapioResponseDTO toResponseDTO(ItemCardapio itemCardapio) {
        return new ItemCardapioResponseDTO(
                itemCardapio.getId(),
                itemCardapio.getRestauranteId(),
                itemCardapio.getNome(),
                itemCardapio.getDescricao(),
                itemCardapio.getPreco(),
                itemCardapio.isDisponivelSomenteLocal(),
                itemCardapio.getFotoPath()
        );
    }
}
