package br.com.fiap.usuarios.application.usecase.usuario;

import br.com.fiap.usuarios.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.usuarios.application.mapper.UsuarioMapper;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.BusinessException;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * Caso de uso: cadastrar um novo usuário. Garante e-mail único, tipo de usuário
 * existente e armazena a senha sempre como hash (nunca em texto plano).
 */
@Component
public class CriarUsuarioUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarUsuarioUseCase(UsuarioRepository usuarioRepository,
                                TipoUsuarioRepository tipoUsuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(UsuarioRequestDTO dto) {
        usuarioRepository.findByEmail(dto.email()).ifPresent(u -> {
            throw new BusinessException("E-mail já cadastrado: " + dto.email());
        });

        tipoUsuarioRepository.findById(dto.tipoUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + dto.tipoUsuarioId()));

        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setDataUltimaAlteracao(LocalDateTime.now());

        var saved = usuarioRepository.save(usuario);
        Assert.state(saved == 1, "Erro ao salvar usuário " + dto.nome());
    }
}
