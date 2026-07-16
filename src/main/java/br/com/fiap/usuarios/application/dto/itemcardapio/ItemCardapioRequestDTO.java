package br.com.fiap.usuarios.application.dto.itemcardapio;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemCardapioRequestDTO(
        @NotBlank(message = "Nome é obrigatório") String nome,
        String descricao,
        @NotNull(message = "Preço é obrigatório") @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero") BigDecimal preco,
        @NotNull(message = "Disponibilidade apenas no local é obrigatória") Boolean disponivelSomenteLocal,
        String fotoPath
) {}
