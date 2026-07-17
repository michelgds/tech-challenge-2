package br.com.fiap.techchallenge.application.usecase.usuario;

import br.com.fiap.techchallenge.application.dto.usuario.UsuarioUpdateDTO;
import br.com.fiap.techchallenge.application.mapper.UsuarioMapper;
import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.BusinessException;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.Usuario;
import br.com.fiap.techchallenge.domain.repository.TipoUsuarioRepository;
import br.com.fiap.techchallenge.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: atualizar dados cadastrais do usuário.
 * ⚠️ Este caso de uso intencionalmente NÃO altera a senha — use
 * {@link AtualizarSenhaUsuarioUseCase} para isso.
 */
@Component
public class AtualizarUsuarioUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public AtualizarUsuarioUseCase(UsuarioRepository usuarioRepository, TipoUsuarioRepository tipoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public void execute(Long id, UsuarioUpdateDTO dto) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        usuarioRepository.findByEmail(dto.email()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new BusinessException("E-mail já cadastrado: " + dto.email());
            }
        });

        tipoUsuarioRepository.findById(dto.tipoUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + dto.tipoUsuarioId()));

        Usuario usuario = UsuarioMapper.toEntity(dto);

        var updated = usuarioRepository.update(usuario, id);
        if (updated == 0) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
    }
}
