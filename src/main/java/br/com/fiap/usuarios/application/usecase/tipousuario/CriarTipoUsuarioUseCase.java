package br.com.fiap.usuarios.application.usecase.tipousuario;

import br.com.fiap.usuarios.application.dto.tipousuario.TipoUsuarioRequestDTO;
import br.com.fiap.usuarios.application.mapper.TipoUsuarioMapper;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.BusinessException;
import br.com.fiap.usuarios.domain.model.TipoUsuario;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Caso de uso: cadastrar um novo tipo de usuário, garantindo nome único.
 */
@Component
public class CriarTipoUsuarioUseCase implements UseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public CriarTipoUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public void execute(TipoUsuarioRequestDTO dto) {
        tipoUsuarioRepository.findByNome(dto.nome()).ifPresent(t -> {
            throw new BusinessException("Tipo de usuário já cadastrado: " + dto.nome());
        });

        TipoUsuario tipoUsuario = TipoUsuarioMapper.toEntity(dto);
        var saved = tipoUsuarioRepository.save(tipoUsuario);
        Assert.state(saved == 1, "Erro ao salvar tipo de usuário " + dto.nome());
    }
}
