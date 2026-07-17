package br.com.fiap.techchallenge.application.dto.restaurante;

import br.com.fiap.techchallenge.application.dto.common.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestauranteRequestDTO(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotNull(message = "Endereço é obrigatório") @Valid EnderecoDTO endereco,
        @NotBlank(message = "Tipo de cozinha é obrigatório") String tipoCozinha,
        @NotBlank(message = "Horário de funcionamento é obrigatório") String horarioFuncionamento,
        @NotNull(message = "Administrador do restaurante é obrigatório") Long adminId
) {}
