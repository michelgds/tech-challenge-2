package br.com.fiap.usuarios.domain.repository;

import br.com.fiap.usuarios.domain.model.Restaurante;

import java.util.List;
import java.util.Optional;

public interface RestauranteRepository {
    Optional<Restaurante> findById(Long id);
    List<Restaurante> findAll(int size, int offset);
    List<Restaurante> findByNome(String nome);
    Integer save(Restaurante restaurante);
    Integer update(Restaurante restaurante, Long id);
    Integer delete(Long id);
    boolean existsById(Long id);
}
