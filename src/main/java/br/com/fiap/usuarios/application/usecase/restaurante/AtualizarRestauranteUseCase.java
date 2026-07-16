package br.com.fiap.usuarios.application.usecase.restaurante;

import br.com.fiap.usuarios.application.dto.restaurante.RestauranteRequestDTO;
import br.com.fiap.usuarios.application.mapper.RestauranteMapper;
import br.com.fiap.usuarios.application.usecase.UseCase;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Restaurante;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: atualizar um restaurante existente.
 */
@Component
public class AtualizarRestauranteUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    public AtualizarRestauranteUseCase(RestauranteRepository restauranteRepository, UsuarioRepository usuarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void execute(Long id, RestauranteRequestDTO dto) {
        restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));

        usuarioRepository.findById(dto.donoId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário (dono) não encontrado com id: " + dto.donoId()));

        Restaurante restaurante = RestauranteMapper.toEntity(dto);
        var updated = restauranteRepository.update(restaurante, id);
        if (updated == 0) {
            throw new ResourceNotFoundException("Restaurante não encontrado com id: " + id);
        }
    }
}
