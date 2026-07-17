package br.com.fiap.techchallenge.application.dto.itemcardapio;

import java.math.BigDecimal;

public record ItemCardapioResponseDTO(
        Long id,
        Long restauranteId,
        String nome,
        String descricao,
        BigDecimal preco,
        boolean disponivelSomenteLocal,
        String fotoPath
) {
}
