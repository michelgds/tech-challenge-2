package br.com.fiap.usuarios.domain.repository;

import br.com.fiap.usuarios.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll(int size, int offset);
    List<Usuario> findByNome(String nome);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByLogin(String login);
    Integer save(Usuario usuario);
    Integer update(Usuario usuario, Long id);
    Integer updateSenha(Long id, String novaSenha);
    Integer delete(Long id);
}

