package br.com.fiap.usuarios.infrastructure.persistence.jdbc;

import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private static final String SELECT_BASE = """
            SELECT u.*, t.nome AS tipo_usuario_nome
            FROM usuarios u
            LEFT JOIN tipos_usuario t ON t.id = u.tipo_usuario_id
            """;

    private final JdbcClient jdbcClient;
    private final UsuarioRowMapper usuarioRowMapper;

    public UsuarioRepositoryImpl(JdbcClient jdbcClient, UsuarioRowMapper usuarioRowMapper) {
        this.jdbcClient = jdbcClient;
        this.usuarioRowMapper = usuarioRowMapper;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jdbcClient.sql(SELECT_BASE + " WHERE u.id = :id")
                .param("id", id)
                .query(usuarioRowMapper)
                .optional();
    }

    @Override
    public List<Usuario> findAll(int size, int offset) {
        return jdbcClient.sql(SELECT_BASE + " ORDER BY u.id LIMIT :size OFFSET :offset")
                .param("size", size)
                .param("offset", offset)
                .query(usuarioRowMapper)
                .list();
    }

    @Override
    public List<Usuario> findByNome(String nome) {
        return jdbcClient.sql(SELECT_BASE + " WHERE u.nome ILIKE :nome")
                .param("nome", "%" + nome + "%")
                .query(usuarioRowMapper)
                .list();
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jdbcClient.sql(SELECT_BASE + " WHERE u.email = :email")
                .param("email", email)
                .query(usuarioRowMapper)
                .optional();
    }

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return jdbcClient.sql(SELECT_BASE + " WHERE u.login = :login")
                .param("login", login)
                .query(usuarioRowMapper)
                .optional();
    }

    @Override
    public Integer save(Usuario usuario) {
        return jdbcClient.sql("""
                INSERT INTO usuarios (nome, email, login, senha, tipo_usuario_id, data_ultima_alteracao,
                    rua, numero, cidade, estado, cep)
                VALUES (:nome, :email, :login, :senha, :tipoUsuarioId, :dataUltimaAlteracao,
                    :rua, :numero, :cidade, :estado, :cep)
                """)
                .param("nome", usuario.getNome())
                .param("email", usuario.getEmail())
                .param("login", usuario.getLogin())
                .param("senha", usuario.getSenha())
                .param("tipoUsuarioId", usuario.getTipoUsuarioId())
                .param("dataUltimaAlteracao", usuario.getDataUltimaAlteracao())
                .param("rua", usuario.getEndereco().getRua())
                .param("numero", usuario.getEndereco().getNumero())
                .param("cidade", usuario.getEndereco().getCidade())
                .param("estado", usuario.getEndereco().getEstado())
                .param("cep", usuario.getEndereco().getCep())
                .update();
    }

    @Override
    public Integer update(Usuario usuario, Long id) {
        return jdbcClient.sql("""
                UPDATE usuarios SET nome = :nome, email = :email, login = :login, tipo_usuario_id = :tipoUsuarioId,
                    data_ultima_alteracao = :dataUltimaAlteracao,
                    rua = :rua, numero = :numero, cidade = :cidade, estado = :estado, cep = :cep
                WHERE id = :id
                """)
                .param("nome", usuario.getNome())
                .param("email", usuario.getEmail())
                .param("login", usuario.getLogin())
                .param("tipoUsuarioId", usuario.getTipoUsuarioId())
                .param("dataUltimaAlteracao", LocalDateTime.now())
                .param("rua", usuario.getEndereco().getRua())
                .param("numero", usuario.getEndereco().getNumero())
                .param("cidade", usuario.getEndereco().getCidade())
                .param("estado", usuario.getEndereco().getEstado())
                .param("cep", usuario.getEndereco().getCep())
                .param("id", id)
                .update();
    }

    @Override
    public Integer updateSenha(Long id, String novaSenha) {
        return jdbcClient.sql("UPDATE usuarios SET senha = :senha, data_ultima_alteracao = :dataUltimaAlteracao WHERE id = :id")
                .param("senha", novaSenha)
                .param("dataUltimaAlteracao", LocalDateTime.now())
                .param("id", id)
                .update();
    }

    @Override
    public Integer delete(Long id) {
        return jdbcClient.sql("DELETE FROM usuarios WHERE id = :id")
                .param("id", id)
                .update();
    }
}
