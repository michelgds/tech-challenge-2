package br.com.fiap.techchallenge.infrastructure.persistence.jdbc;

import br.com.fiap.techchallenge.domain.model.TipoUsuario;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TipoUsuarioRowMapper implements RowMapper<TipoUsuario> {

    @Override
    public TipoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TipoUsuario(
                rs.getLong("id"),
                rs.getString("nome")
        );
    }
}
