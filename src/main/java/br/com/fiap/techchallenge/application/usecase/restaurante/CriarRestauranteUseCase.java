package br.com.fiap.techchallenge.application.usecase.restaurante;

import br.com.fiap.techchallenge.application.dto.restaurante.RestauranteRequestDTO;
import br.com.fiap.techchallenge.application.mapper.RestauranteMapper;
import br.com.fiap.techchallenge.application.usecase.UseCase;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.Restaurante;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import br.com.fiap.techchallenge.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CriarRestauranteUseCase implements UseCase {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    public CriarRestauranteUseCase(RestauranteRepository restauranteRepository, UsuarioRepository usuarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void execute(RestauranteRequestDTO dto) {
        usuarioRepository.findById(dto.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário (admin) não encontrado com id: " + dto.adminId()));

        Restaurante restaurante = RestauranteMapper.toEntity(dto);
        var saved = restauranteRepository.save(restaurante);
        Assert.state(saved == 1, "Erro ao salvar restaurante " + dto.nome());
    }
}
