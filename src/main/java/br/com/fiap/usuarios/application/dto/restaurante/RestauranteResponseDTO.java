package br.com.fiap.usuarios.application.dto.restaurante;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;

public record RestauranteResponseDTO(
        Long id,
        String nome,
        EnderecoDTO endereco,
        String tipoCozinha,
        String horarioFuncionamento,
        Long donoId
) {
}
