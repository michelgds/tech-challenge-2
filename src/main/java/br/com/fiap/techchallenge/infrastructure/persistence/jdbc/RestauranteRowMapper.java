package br.com.fiap.techchallenge.infrastructure.persistence.jdbc;

import br.com.fiap.techchallenge.domain.model.Endereco;
import br.com.fiap.techchallenge.domain.model.Restaurante;
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
                rs.getLong("admin_id")
        );
    }
}
