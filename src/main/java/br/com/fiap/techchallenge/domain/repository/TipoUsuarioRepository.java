package br.com.fiap.techchallenge.domain.repository;

import br.com.fiap.techchallenge.domain.model.TipoUsuario;

import java.util.List;
import java.util.Optional;

public interface TipoUsuarioRepository {
    Optional<TipoUsuario> findById(Long id);
    List<TipoUsuario> findAll();
    Optional<TipoUsuario> findByNome(String nome);
    Integer save(TipoUsuario tipoUsuario);
    Integer update(TipoUsuario tipoUsuario, Long id);
    Integer delete(Long id);
    boolean existsUsuariosComTipo(Long tipoUsuarioId);
}
