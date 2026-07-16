package br.com.fiap.usuarios.application.usecase.tipousuario;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.model.TipoUsuario;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso: listar todos os tipos de usuário.
 */
@Component
public class ListarTiposUsuarioUseCase implements UseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public ListarTiposUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public List<TipoUsuario> execute() {
        return tipoUsuarioRepository.findAll();
    }
}
