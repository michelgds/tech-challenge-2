package br.com.fiap.techchallenge.application.usecase.usuario;

import br.com.fiap.techchallenge.application.dto.usuario.LoginRequestDTO;
import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.AuthenticationException;
import br.com.fiap.techchallenge.domain.model.Usuario;
import br.com.fiap.techchallenge.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: autenticar um usuário validando login e senha (hash) informados.
 */
@Component
public class AutenticarUsuarioUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AutenticarUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario execute(LoginRequestDTO dto) {
        return usuarioRepository.findByLogin(dto.login())
                .filter(usuario -> passwordEncoder.matches(dto.senha(), usuario.getSenha()))
                .orElseThrow(AuthenticationException::new);
    }
}
