package br.com.fiap.techchallenge.infrastructure.persistence.jdbc;

import br.com.fiap.techchallenge.domain.model.TipoUsuario;
import br.com.fiap.techchallenge.domain.repository.TipoUsuarioRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoUsuarioRepositoryImpl implements TipoUsuarioRepository {

    private final JdbcClient jdbcClient;
    private final TipoUsuarioRowMapper tipoUsuarioRowMapper;

    public TipoUsuarioRepositoryImpl(JdbcClient jdbcClient, TipoUsuarioRowMapper tipoUsuarioRowMapper) {
        this.jdbcClient = jdbcClient;
        this.tipoUsuarioRowMapper = tipoUsuarioRowMapper;
    }

    @Override
    public Optional<TipoUsuario> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM tipos_usuario WHERE id = :id")
                .param("id", id)
                .query(tipoUsuarioRowMapper)
                .optional();
    }

    @Override
    public List<TipoUsuario> findAll() {
        return jdbcClient.sql("SELECT * FROM tipos_usuario ORDER BY nome")
                .query(tipoUsuarioRowMapper)
                .list();
    }

    @Override
    public Optional<TipoUsuario> findByNome(String nome) {
        return jdbcClient.sql("SELECT * FROM tipos_usuario WHERE nome = :nome")
                .param("nome", nome)
                .query(tipoUsuarioRowMapper)
                .optional();
    }

    @Override
    public Integer save(TipoUsuario tipoUsuario) {
        return jdbcClient.sql("INSERT INTO tipos_usuario (nome) VALUES (:nome)")
                .param("nome", tipoUsuario.getNome())
                .update();
    }

    @Override
    public Integer update(TipoUsuario tipoUsuario, Long id) {
        return jdbcClient.sql("UPDATE tipos_usuario SET nome = :nome WHERE id = :id")
                .param("nome", tipoUsuario.getNome())
                .param("id", id)
                .update();
    }

    @Override
    public Integer delete(Long id) {
        return jdbcClient.sql("DELETE FROM tipos_usuario WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public boolean existsUsuariosComTipo(Long tipoUsuarioId) {
        Integer count = jdbcClient.sql("SELECT COUNT(*) FROM usuarios WHERE tipo_usuario_id = :id")
                .param("id", tipoUsuarioId)
                .query(Integer.class)
                .single();
        return count != null && count > 0;
    }
}
