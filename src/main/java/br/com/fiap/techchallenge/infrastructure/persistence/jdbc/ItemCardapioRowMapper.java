package br.com.fiap.techchallenge.infrastructure.persistence.jdbc;

import br.com.fiap.techchallenge.domain.model.ItemCardapio;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ItemCardapioRowMapper implements RowMapper<ItemCardapio> {

    @Override
    public ItemCardapio mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ItemCardapio(
                rs.getLong("id"),
                rs.getLong("restaurante_id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getBigDecimal("preco"),
                rs.getBoolean("disponivel_somente_local"),
                rs.getString("foto_path")
        );
    }
}
