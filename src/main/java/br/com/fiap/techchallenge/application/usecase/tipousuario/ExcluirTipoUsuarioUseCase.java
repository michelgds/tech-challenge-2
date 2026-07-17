package br.com.fiap.techchallenge.application.usecase.tipousuario;

import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.BusinessException;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

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
