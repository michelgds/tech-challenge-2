package br.com.fiap.techchallenge.application.usecase.tipousuario;

import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.model.TipoUsuario;
import br.com.fiap.techchallenge.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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
