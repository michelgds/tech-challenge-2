package br.com.fiap.usuarios.application.usecase.usuario;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: excluir um usuário existente.
 */
@Component
public class ExcluirUsuarioUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;

    public ExcluirUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void execute(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        usuarioRepository.delete(id);
    }
}
