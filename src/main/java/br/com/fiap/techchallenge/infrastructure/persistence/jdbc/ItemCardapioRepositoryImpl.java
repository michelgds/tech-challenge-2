package br.com.fiap.techchallenge.infrastructure.persistence.jdbc;

import br.com.fiap.techchallenge.domain.model.ItemCardapio;
import br.com.fiap.techchallenge.domain.repository.ItemCardapioRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ItemCardapioRepositoryImpl implements ItemCardapioRepository {

    private final JdbcClient jdbcClient;
    private final ItemCardapioRowMapper itemCardapioRowMapper;

    public ItemCardapioRepositoryImpl(JdbcClient jdbcClient, ItemCardapioRowMapper itemCardapioRowMapper) {
        this.jdbcClient = jdbcClient;
        this.itemCardapioRowMapper = itemCardapioRowMapper;
    }

    @Override
    public Optional<ItemCardapio> findById(Long id) {
        return jdbcClient.sql("SELECT * FROM itens_cardapio WHERE id = :id")
                .param("id", id)
                .query(itemCardapioRowMapper)
                .optional();
    }

    @Override
    public List<ItemCardapio> findByRestauranteId(Long restauranteId, int size, int offset) {
        return jdbcClient.sql("SELECT * FROM itens_cardapio WHERE restaurante_id = :restauranteId ORDER BY id LIMIT :size OFFSET :offset")
                .param("restauranteId", restauranteId)
                .param("size", size)
                .param("offset", offset)
                .query(itemCardapioRowMapper)
                .list();
    }

    @Override
    public Integer save(ItemCardapio itemCardapio) {
        return jdbcClient.sql("""
                INSERT INTO itens_cardapio (restaurante_id, nome, descricao, preco, disponivel_somente_local, foto_path)
                VALUES (:restauranteId, :nome, :descricao, :preco, :disponivelSomenteLocal, :fotoPath)
                """)
                .param("restauranteId", itemCardapio.getRestauranteId())
                .param("nome", itemCardapio.getNome())
                .param("descricao", itemCardapio.getDescricao())
                .param("preco", itemCardapio.getPreco())
                .param("disponivelSomenteLocal", itemCardapio.isDisponivelSomenteLocal())
                .param("fotoPath", itemCardapio.getFotoPath())
                .update();
    }

    @Override
    public Integer update(ItemCardapio itemCardapio, Long id) {
        return jdbcClient.sql("""
                UPDATE itens_cardapio SET nome = :nome, descricao = :descricao, preco = :preco,
                    disponivel_somente_local = :disponivelSomenteLocal, foto_path = :fotoPath
                WHERE id = :id
                """)
                .param("nome", itemCardapio.getNome())
                .param("descricao", itemCardapio.getDescricao())
                .param("preco", itemCardapio.getPreco())
                .param("disponivelSomenteLocal", itemCardapio.isDisponivelSomenteLocal())
                .param("fotoPath", itemCardapio.getFotoPath())
                .param("id", id)
                .update();
    }

    @Override
    public Integer delete(Long id) {
        return jdbcClient.sql("DELETE FROM itens_cardapio WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public Integer deleteByRestauranteId(Long restauranteId) {
        return jdbcClient.sql("DELETE FROM itens_cardapio WHERE restaurante_id = :restauranteId")
                .param("restauranteId", restauranteId)
                .update();
    }
}
