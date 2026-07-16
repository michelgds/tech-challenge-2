package br.com.fiap.usuarios.application.usecase.tipousuario;

import br.com.fiap.usuarios.application.dto.tipousuario.TipoUsuarioRequestDTO;
import br.com.fiap.usuarios.application.mapper.TipoUsuarioMapper;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.BusinessException;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.TipoUsuario;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: atualizar um tipo de usuário existente.
 */
@Component
public class AtualizarTipoUsuarioUseCase implements UseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public AtualizarTipoUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public void execute(Long id, TipoUsuarioRequestDTO dto) {
        tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id));

        tipoUsuarioRepository.findByNome(dto.nome()).ifPresent(t -> {
            if (!t.getId().equals(id)) {
                throw new BusinessException("Tipo de usuário já cadastrado: " + dto.nome());
            }
        });

        TipoUsuario tipoUsuario = TipoUsuarioMapper.toEntity(dto);
        var updated = tipoUsuarioRepository.update(tipoUsuario, id);
        if (updated == 0) {
            throw new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id);
        }
    }
}
