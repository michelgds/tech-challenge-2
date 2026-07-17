package br.com.fiap.techchallenge.application.usecase.usuario;

import br.com.fiap.techchallenge.application.dto.usuario.SenhaUpdateDTO;
import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.BusinessException;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.Usuario;
import br.com.fiap.techchallenge.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
