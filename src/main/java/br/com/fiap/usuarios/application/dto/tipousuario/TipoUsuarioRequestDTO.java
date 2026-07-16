package br.com.fiap.usuarios.application.dto.tipousuario;

import jakarta.validation.constraints.NotBlank;

public record TipoUsuarioRequestDTO(
        @NotBlank(message = "Nome do tipo é obrigatório") String nome
) {}
