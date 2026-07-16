package br.com.fiap.usuarios.application.usecase.usuario;

import br.com.fiap.usuarios.application.dto.usuario.SenhaUpdateDTO;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.BusinessException;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Caso de uso exclusivo para troca de senha. Exige validação da senha atual
 * (comparada via hash) e persiste a nova senha também como hash.
 */
@Component
public class AtualizarSenhaUsuarioUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AtualizarSenhaUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(Long id, SenhaUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        String novaSenhaHash = passwordEncoder.encode(dto.novaSenha());
        var updated = usuarioRepository.updateSenha(id, novaSenhaHash);
        if (updated == 0) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
    }
}
