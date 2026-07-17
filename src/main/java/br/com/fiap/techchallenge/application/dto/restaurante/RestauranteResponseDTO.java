package br.com.fiap.techchallenge.application.dto.restaurante;

import br.com.fiap.techchallenge.application.dto.common.EnderecoDTO;

public record RestauranteResponseDTO(
        Long id,
        String nome,
        EnderecoDTO endereco,
        String tipoCozinha,
        String horarioFuncionamento,
        Long adminId
) {
}
