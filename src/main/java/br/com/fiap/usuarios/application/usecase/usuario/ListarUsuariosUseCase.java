package br.com.fiap.usuarios.application.usecase.usuario;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso: listar usuários, com paginação e filtro opcional por nome.
 */
@Component
public class ListarUsuariosUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;

    public ListarUsuariosUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> execute(int page, int size, String nome) {
        if (nome != null && !nome.isBlank()) {
            return usuarioRepository.findByNome(nome);
        }
        int offset = (page - 1) * size;
        return usuarioRepository.findAll(size, offset);
    }
}
