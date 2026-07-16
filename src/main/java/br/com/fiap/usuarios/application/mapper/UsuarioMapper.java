package br.com.fiap.usuarios.application.mapper;

import br.com.fiap.usuarios.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.usuarios.application.dto.usuario.UsuarioResponseDTO;
import br.com.fiap.usuarios.application.dto.usuario.UsuarioUpdateDTO;
import br.com.fiap.usuarios.domain.model.Usuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLogin(dto.login());
        usuario.setSenha(dto.senha());
        usuario.setTipoUsuarioId(dto.tipoUsuarioId());
        usuario.setEndereco(EnderecoMapper.toDomain(dto.endereco()));
        return usuario;
    }

    public static Usuario toEntity(UsuarioUpdateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLogin(dto.login());
        usuario.setTipoUsuarioId(dto.tipoUsuarioId());
        usuario.setEndereco(EnderecoMapper.toDomain(dto.endereco()));
        return usuario;
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin(),
                usuario.getTipoUsuarioId(),
                usuario.getTipoUsuarioNome(),
                usuario.getDataUltimaAlteracao(),
                EnderecoMapper.toDTO(usuario.getEndereco())
        );
    }
}
