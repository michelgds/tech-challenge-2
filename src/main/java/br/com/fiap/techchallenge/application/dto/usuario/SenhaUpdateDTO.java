package br.com.fiap.techchallenge.application.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record SenhaUpdateDTO(
        @NotBlank(message = "Senha atual é obrigatória") String senhaAtual,
        @NotBlank(message = "Nova senha é obrigatória") String novaSenha
) {}

