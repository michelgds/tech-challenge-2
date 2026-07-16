package br.com.fiap.usuarios.application.usecase.usuario;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: buscar um usuário por ID, lançando exceção de domínio quando não encontrado.
 */
@Component
public class BuscarUsuarioPorIdUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioPorIdUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
    }
}
