package br.com.fiap.usuarios.application.dto.usuario;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;

import java.time.LocalDateTime;

/**
 * DTO de resposta da API. Nunca inclui a senha (nem seu hash), evitando que o
 * contrato HTTP dependa ou vaze detalhes do modelo de domínio/persistência.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String login,
        Long tipoUsuarioId,
        String tipoUsuarioNome,
        LocalDateTime dataUltimaAlteracao,
        EnderecoDTO endereco
) {
}
