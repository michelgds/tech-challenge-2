package br.com.fiap.usuarios.application.mapper;

import br.com.fiap.usuarios.application.dto.tipousuario.TipoUsuarioRequestDTO;
import br.com.fiap.usuarios.application.dto.tipousuario.TipoUsuarioResponseDTO;
import br.com.fiap.usuarios.domain.model.TipoUsuario;

public final class TipoUsuarioMapper {

    private TipoUsuarioMapper() {
    }

    public static TipoUsuario toEntity(TipoUsuarioRequestDTO dto) {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setNome(dto.nome());
        return tipoUsuario;
    }

    public static TipoUsuarioResponseDTO toResponseDTO(TipoUsuario tipoUsuario) {
        return new TipoUsuarioResponseDTO(tipoUsuario.getId(), tipoUsuario.getNome());
    }
}
