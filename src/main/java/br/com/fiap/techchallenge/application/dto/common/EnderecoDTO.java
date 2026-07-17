package br.com.fiap.techchallenge.application.dto.common;

import jakarta.validation.constraints.NotBlank;

public record EnderecoDTO(
        @NotBlank(message = "Rua é obrigatória") String rua,
        @NotBlank(message = "Número é obrigatório") String numero,
        @NotBlank(message = "Cidade é obrigatória") String cidade,
        @NotBlank(message = "Estado é obrigatório") String estado,
        @NotBlank(message = "CEP é obrigatório") String cep
) {}

