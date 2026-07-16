package br.com.fiap.usuarios.application.usecase.restaurante;

import br.com.fiap.usuarios.application.dto.restaurante.RestauranteRequestDTO;
import br.com.fiap.usuarios.application.mapper.RestauranteMapper;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Restaurante;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Caso de uso: cadastrar um novo restaurante, garantindo que o dono informado exista.
 */
@Component
public class CriarRestauranteUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    public CriarRestauranteUseCase(RestauranteRepository restauranteRepository, UsuarioRepository usuarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void execute(RestauranteRequestDTO dto) {
        usuarioRepository.findById(dto.donoId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário (dono) não encontrado com id: " + dto.donoId()));

        Restaurante restaurante = RestauranteMapper.toEntity(dto);
        var saved = restauranteRepository.save(restaurante);
        Assert.state(saved == 1, "Erro ao salvar restaurante " + dto.nome());
    }
}
