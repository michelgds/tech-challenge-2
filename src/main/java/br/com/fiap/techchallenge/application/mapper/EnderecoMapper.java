package br.com.fiap.techchallenge.application.mapper;

import br.com.fiap.techchallenge.application.dto.common.EnderecoDTO;
import br.com.fiap.techchallenge.domain.model.Endereco;

public final class EnderecoMapper {

    private EnderecoMapper() {
    }

    public static Endereco toDomain(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Endereco(dto.rua(), dto.numero(), dto.cidade(), dto.estado(), dto.cep());
    }

    public static EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDTO(endereco.getRua(), endereco.getNumero(), endereco.getCidade(),
                endereco.getEstado(), endereco.getCep());
    }
}
