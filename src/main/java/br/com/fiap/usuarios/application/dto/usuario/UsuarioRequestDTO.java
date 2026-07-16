package br.com.fiap.usuarios.application.dto.usuario;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequestDTO(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
        @NotBlank(message = "Login é obrigatório") String login,
        @NotBlank(message = "Senha é obrigatória") String senha,
        @NotNull(message = "Tipo de usuário é obrigatório") Long tipoUsuarioId,
        @NotNull(message = "Endereço é obrigatório") @Valid EnderecoDTO endereco
) {}

