package br.com.fiap.usuarios.application.usecase.tipousuario;

import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.BusinessException;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: excluir um tipo de usuário, bloqueando a exclusão caso ainda
 * existam usuários vinculados a ele.
 */
@Component
public class ExcluirTipoUsuarioUseCase implements UseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public ExcluirTipoUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public void execute(Long id) {
        tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id));

        if (tipoUsuarioRepository.existsUsuariosComTipo(id)) {
            throw new BusinessException("Não é possível excluir: existem usuários associados a este tipo");
        }

        tipoUsuarioRepository.delete(id);
    }
}
