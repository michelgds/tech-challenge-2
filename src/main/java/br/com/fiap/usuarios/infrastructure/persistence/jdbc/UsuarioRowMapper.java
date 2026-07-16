package br.com.fiap.usuarios.infrastructure.persistence.jdbc;

import br.com.fiap.usuarios.domain.model.Endereco;
import br.com.fiap.usuarios.domain.model.Usuario;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class UsuarioRowMapper implements RowMapper<Usuario> {

    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Endereco endereco = new Endereco(
                rs.getString("rua"),
                rs.getString("numero"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getString("cep")
        );

        LocalDateTime dataUltimaAlteracao = rs.getTimestamp("data_ultima_alteracao") != null
                ? rs.getTimestamp("data_ultima_alteracao").toLocalDateTime()
                : null;

        return new Usuario(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getLong("tipo_usuario_id"),
                rs.getString("tipo_usuario_nome"),
                dataUltimaAlteracao,
                endereco
        );
    }
}

