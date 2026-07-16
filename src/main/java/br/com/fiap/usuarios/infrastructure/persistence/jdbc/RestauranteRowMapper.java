package br.com.fiap.usuarios.infrastructure.persistence.jdbc;

import br.com.fiap.usuarios.domain.model.Endereco;
import br.com.fiap.usuarios.domain.model.Restaurante;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RestauranteRowMapper implements RowMapper<Restaurante> {

    @Override
    public Restaurante mapRow(ResultSet rs, int rowNum) throws SQLException {
        Endereco endereco = new Endereco(
                rs.getString("rua"),
                rs.getString("numero"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getString("cep")
        );

        return new Restaurante(
                rs.getLong("id"),
                rs.getString("nome"),
                endereco,
                rs.getString("tipo_cozinha"),
                rs.getString("horario_funcionamento"),
                rs.getLong("dono_id")
        );
    }
}
