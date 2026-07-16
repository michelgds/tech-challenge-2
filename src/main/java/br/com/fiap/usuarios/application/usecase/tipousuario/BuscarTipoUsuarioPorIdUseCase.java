package br.com.fiap.usuarios.application.usecase.tipousuario;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.TipoUsuario;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: buscar um tipo de usuário por ID.
 */
@Component
public class BuscarTipoUsuarioPorIdUseCase implements UseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public BuscarTipoUsuarioPorIdUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public TipoUsuario execute(Long id) {
        return tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id));
    }
}
