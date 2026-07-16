package br.com.fiap.usuarios.application.usecase.restaurante;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;
import br.com.fiap.usuarios.application.dto.restaurante.RestauranteRequestDTO;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Endereco;
import br.com.fiap.usuarios.domain.model.Restaurante;
import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.RestauranteRepository;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteUseCasesTest {

    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    private ListarRestaurantesUseCase listarRestaurantesUseCase;
    private BuscarRestaurantePorIdUseCase buscarRestaurantePorIdUseCase;
    private CriarRestauranteUseCase criarRestauranteUseCase;
    private AtualizarRestauranteUseCase atualizarRestauranteUseCase;
    private ExcluirRestauranteUseCase excluirRestauranteUseCase;

    @BeforeEach
    void setUp() {
        listarRestaurantesUseCase = new ListarRestaurantesUseCase(restauranteRepository);
        buscarRestaurantePorIdUseCase = new BuscarRestaurantePorIdUseCase(restauranteRepository);
        criarRestauranteUseCase = new CriarRestauranteUseCase(restauranteRepository, usuarioRepository);
        atualizarRestauranteUseCase = new AtualizarRestauranteUseCase(restauranteRepository, usuarioRepository);
        excluirRestauranteUseCase = new ExcluirRestauranteUseCase(restauranteRepository);
    }

    @Test
    void listarShouldDelegateUsingCalculatedOffsetWhenNomeIsAbsent() {
        List<Restaurante> restaurantes = List.of(restaurante(1L, "Cantina"));
        when(restauranteRepository.findAll(4, 4)).thenReturn(restaurantes);

        assertThat(listarRestaurantesUseCase.execute(2, 4, null)).isEqualTo(restaurantes);
        verify(restauranteRepository).findAll(4, 4);
    }

    @Test
    void listarShouldDelegateToFindByNomeWhenNomeIsPresent() {
        List<Restaurante> restaurantes = List.of(restaurante(1L, "Cantina"));
        when(restauranteRepository.findByNome("Cant")).thenReturn(restaurantes);

        assertThat(listarRestaurantesUseCase.execute(1, 10, "Cant")).isEqualTo(restaurantes);
        verify(restauranteRepository).findByNome("Cant");
    }

    @Test
    void buscarPorIdShouldReturnRestauranteWhenFound() {
        Restaurante restaurante = restaurante(1L, "Cantina");
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        assertThat(buscarRestaurantePorIdUseCase.execute(1L)).isEqualTo(restaurante);
    }

    @Test
    void buscarPorIdShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarRestaurantePorIdUseCase.execute(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 99");
    }

    @Test
    void criarShouldPersistRestauranteWhenDonoExists() {
        RestauranteRequestDTO dto = restauranteDto("Cantina Nova", 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L)));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(1);

        criarRestauranteUseCase.execute(dto);

        ArgumentCaptor<Restaurante> captor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(captor.capture());
        Restaurante salvo = captor.getValue();
        assertThat(salvo.getNome()).isEqualTo(dto.nome());
        assertThat(salvo.getDonoId()).isEqualTo(dto.donoId());
        assertThat(salvo.getTipoCozinha()).isEqualTo(dto.tipoCozinha());
        assertThat(salvo.getHorarioFuncionamento()).isEqualTo(dto.horarioFuncionamento());
        assertThat(salvo.getEndereco()).isEqualTo(new Endereco("Rua B", "15", "São Paulo", "SP", "02000-000"));
    }

    @Test
    void criarShouldThrowWhenDonoDoesNotExist() {
        RestauranteRequestDTO dto = restauranteDto("Cantina Nova", 15L);
        when(usuarioRepository.findById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> criarRestauranteUseCase.execute(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário (dono) não encontrado com id: 15");

        verify(restauranteRepository, never()).save(any());
    }

    @Test
    void atualizarShouldPersistChangesWhenRestauranteAndDonoExist() {
        RestauranteRequestDTO dto = restauranteDto("Cantina Atualizada", 1L);
        when(restauranteRepository.findById(5L)).thenReturn(Optional.of(restaurante(5L, "Cantina Antiga")));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L)));
        when(restauranteRepository.update(any(Restaurante.class), eq(5L))).thenReturn(1);

        atualizarRestauranteUseCase.execute(5L, dto);

        ArgumentCaptor<Restaurante> captor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).update(captor.capture(), eq(5L));
        Restaurante atualizado = captor.getValue();
        assertThat(atualizado.getNome()).isEqualTo(dto.nome());
        assertThat(atualizado.getDonoId()).isEqualTo(dto.donoId());
        assertThat(atualizado.getEndereco()).isEqualTo(new Endereco("Rua B", "15", "São Paulo", "SP", "02000-000"));
    }

    @Test
    void atualizarShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.findById(21L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarRestauranteUseCase.execute(21L, restauranteDto("Cantina", 1L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 21");
    }

    @Test
    void atualizarShouldThrowWhenDonoDoesNotExist() {
        when(restauranteRepository.findById(21L)).thenReturn(Optional.of(restaurante(21L, "Cantina")));
        when(usuarioRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarRestauranteUseCase.execute(21L, restauranteDto("Cantina", 50L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário (dono) não encontrado com id: 50");

        verify(restauranteRepository, never()).update(any(), eq(21L));
    }

    @Test
    void excluirShouldRemoveRestauranteWhenItExists() {
        when(restauranteRepository.findById(6L)).thenReturn(Optional.of(restaurante(6L, "Cantina")));

        excluirRestauranteUseCase.execute(6L);

        verify(restauranteRepository).delete(6L);
    }

    @Test
    void excluirShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.findById(33L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> excluirRestauranteUseCase.execute(33L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 33");

        verify(restauranteRepository, never()).delete(any());
    }

    private RestauranteRequestDTO restauranteDto(String nome, Long donoId) {
        return new RestauranteRequestDTO(
                nome,
                new EnderecoDTO("Rua B", "15", "São Paulo", "SP", "02000-000"),
                "Italiana",
                "11h às 22h",
                donoId
        );
    }

    private Restaurante restaurante(Long id, String nome) {
        return new Restaurante(id, nome, new Endereco("Rua B", "15", "São Paulo", "SP", "02000-000"), "Italiana", "11h às 22h", 1L);
    }

    private Usuario usuario(Long id) {
        return new Usuario(id, "Admin", "admin@email.com", "admin", "hash", 1L, "DONO_RESTAURANTE", null,
                new Endereco("Rua A", "10", "São Paulo", "SP", "01000-000"));
    }
}
