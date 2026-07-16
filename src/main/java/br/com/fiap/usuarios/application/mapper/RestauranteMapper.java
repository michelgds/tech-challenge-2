package br.com.fiap.usuarios.application.mapper;

import br.com.fiap.usuarios.application.dto.restaurante.RestauranteRequestDTO;
import br.com.fiap.usuarios.application.dto.restaurante.RestauranteResponseDTO;
import br.com.fiap.usuarios.domain.model.Restaurante;

public final class RestauranteMapper {

    private RestauranteMapper() {
    }

    public static Restaurante toEntity(RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.nome());
        restaurante.setEndereco(EnderecoMapper.toDomain(dto.endereco()));
        restaurante.setTipoCozinha(dto.tipoCozinha());
        restaurante.setHorarioFuncionamento(dto.horarioFuncionamento());
        restaurante.setDonoId(dto.donoId());
        return restaurante;
    }

    public static RestauranteResponseDTO toResponseDTO(Restaurante restaurante) {
        return new RestauranteResponseDTO(
                restaurante.getId(),
                restaurante.getNome(),
                EnderecoMapper.toDTO(restaurante.getEndereco()),
                restaurante.getTipoCozinha(),
                restaurante.getHorarioFuncionamento(),
                restaurante.getDonoId()
        );
    }
}
