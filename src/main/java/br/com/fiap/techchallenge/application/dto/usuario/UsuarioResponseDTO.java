package br.com.fiap.techchallenge.application.dto.usuario;

import br.com.fiap.techchallenge.application.dto.common.EnderecoDTO;

import java.time.LocalDateTime;

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
