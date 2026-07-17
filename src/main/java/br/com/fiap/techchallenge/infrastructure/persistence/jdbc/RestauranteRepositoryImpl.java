package br.com.fiap.techchallenge.infrastructure.persistence.jdbc;

import br.com.fiap.techchallenge.domain.model.Restaurante;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private final JdbcClient jdbcClient;
    private final RestauranteRowMapper restauranteRowMapper;

    public RestauranteRepositoryImpl(JdbcClient jdbcClient, RestauranteRowMapper restauranteRowMapper) {
        this.jdbcClient = jdbcClient;
        this.restauranteRowMapper = restauranteRowMapper;
    }

    @Override
    public Optional<Restaurante> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM restaurantes WHERE id = :id")
                .param("id", id)
                .query(restauranteRowMapper)
                .optional();
    }

    @Override
    public List<Restaurante> findAll(int size, int offset) {
        return jdbcClient.sql("SELECT * FROM restaurantes ORDER BY id LIMIT :size OFFSET :offset")
                .param("size", size)
                .param("offset", offset)
                .query(restauranteRowMapper)
                .list();
    }

    @Override
    public List<Restaurante> findByNome(String nome) {
        return jdbcClient.sql("SELECT * FROM restaurantes WHERE nome ILIKE :nome")
                .param("nome", "%" + nome + "%")
                .query(restauranteRowMapper)
                .list();
    }

    @Override
    public Integer save(Restaurante restaurante) {
        return jdbcClient.sql("""
                INSERT INTO restaurantes (nome, rua, numero, cidade, estado, cep, tipo_cozinha, horario_funcionamento, admin_id)
                VALUES (:nome, :rua, :numero, :cidade, :estado, :cep, :tipoCozinha, :horarioFuncionamento, :adminId)
                """)
                .param("nome", restaurante.getNome())
                .param("rua", restaurante.getEndereco().getRua())
                .param("numero", restaurante.getEndereco().getNumero())
                .param("cidade", restaurante.getEndereco().getCidade())
                .param("estado", restaurante.getEndereco().getEstado())
                .param("cep", restaurante.getEndereco().getCep())
                .param("tipoCozinha", restaurante.getTipoCozinha())
                .param("horarioFuncionamento", restaurante.getHorarioFuncionamento())
                .param("adminId", restaurante.getAdminId())
                .update();
    }

    @Override
    public Integer update(Restaurante restaurante, Long id) {
        return jdbcClient.sql("""
                UPDATE restaurantes SET nome = :nome, rua = :rua, numero = :numero, cidade = :cidade,
                    estado = :estado, cep = :cep, tipo_cozinha = :tipoCozinha,
                    horario_funcionamento = :horarioFuncionamento, admin_id = :adminId
                WHERE id = :id
                """)
                .param("nome", restaurante.getNome())
                .param("rua", restaurante.getEndereco().getRua())
                .param("numero", restaurante.getEndereco().getNumero())
                .param("cidade", restaurante.getEndereco().getCidade())
                .param("estado", restaurante.getEndereco().getEstado())
                .param("cep", restaurante.getEndereco().getCep())
                .param("tipoCozinha", restaurante.getTipoCozinha())
                .param("horarioFuncionamento", restaurante.getHorarioFuncionamento())
                .param("adminId", restaurante.getAdminId())
                .param("id", id)
                .update();
    }

    @Override
    public Integer delete(Long id) {
        return jdbcClient.sql("DELETE FROM restaurantes WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcClient.sql("SELECT COUNT(*) FROM restaurantes WHERE id = :id")
                .param("id", id)
                .query(Integer.class)
                .single();
        return count != null && count > 0;
    }
}
