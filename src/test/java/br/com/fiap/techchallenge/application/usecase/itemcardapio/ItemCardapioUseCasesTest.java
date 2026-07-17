package br.com.fiap.techchallenge.application.usecase.itemcardapio;

import br.com.fiap.techchallenge.application.dto.itemcardapio.ItemCardapioRequestDTO;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.ItemCardapio;
import br.com.fiap.techchallenge.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallenge.domain.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class ItemCardapioUseCasesTest {

    @Mock
    private ItemCardapioRepository itemCardapioRepository;
    @Mock
    private RestauranteRepository restauranteRepository;

    private ListarItensCardapioUseCase listarItensCardapioUseCase;
    private BuscarItemCardapioPorIdUseCase buscarItemCardapioPorIdUseCase;
    private CriarItemCardapioUseCase criarItemCardapioUseCase;
    private AtualizarItemCardapioUseCase atualizarItemCardapioUseCase;
    private ExcluirItemCardapioUseCase excluirItemCardapioUseCase;

    @BeforeEach
    void setUp() {
        listarItensCardapioUseCase = new ListarItensCardapioUseCase(itemCardapioRepository, restauranteRepository);
        buscarItemCardapioPorIdUseCase = new BuscarItemCardapioPorIdUseCase(itemCardapioRepository);
        criarItemCardapioUseCase = new CriarItemCardapioUseCase(itemCardapioRepository, restauranteRepository);
        atualizarItemCardapioUseCase = new AtualizarItemCardapioUseCase(itemCardapioRepository, restauranteRepository);
        excluirItemCardapioUseCase = new ExcluirItemCardapioUseCase(itemCardapioRepository, restauranteRepository);
    }

    @Test
    void listarShouldReturnItemsWhenRestauranteExists() {
        List<ItemCardapio> itens = List.of(item(1L, 10L, "Lasanha"));
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findByRestauranteId(10L, 3, 3)).thenReturn(itens);

        assertThat(listarItensCardapioUseCase.execute(10L, 2, 3)).isEqualTo(itens);
        verify(itemCardapioRepository).findByRestauranteId(10L, 3, 3);
    }

    @Test
    void listarShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> listarItensCardapioUseCase.execute(10L, 1, 10))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 10");

        verify(itemCardapioRepository, never()).findByRestauranteId(any(), any(Integer.class), any(Integer.class));
    }

    @Test
    void buscarPorIdShouldReturnItemWhenFound() {
        ItemCardapio item = item(1L, 10L, "Lasanha");
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThat(buscarItemCardapioPorIdUseCase.execute(1L)).isEqualTo(item);
    }

    @Test
    void buscarPorIdShouldThrowWhenItemDoesNotExist() {
        when(itemCardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarItemCardapioPorIdUseCase.execute(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Item do cardápio não encontrado com id: 99");
    }

    @Test
    void criarShouldPersistItemWhenRestauranteExists() {
        ItemCardapioRequestDTO dto = itemDto("Lasanha");
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(1);

        criarItemCardapioUseCase.execute(10L, dto);

        ArgumentCaptor<ItemCardapio> captor = ArgumentCaptor.forClass(ItemCardapio.class);
        verify(itemCardapioRepository).save(captor.capture());
        ItemCardapio salvo = captor.getValue();
        assertThat(salvo.getRestauranteId()).isEqualTo(10L);
        assertThat(salvo.getNome()).isEqualTo(dto.nome());
        assertThat(salvo.getDescricao()).isEqualTo(dto.descricao());
        assertThat(salvo.getPreco()).isEqualByComparingTo(dto.preco());
        assertThat(salvo.isDisponivelSomenteLocal()).isEqualTo(dto.disponivelSomenteLocal());
        assertThat(salvo.getFotoPath()).isEqualTo(dto.fotoPath());
    }

    @Test
    void criarShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> criarItemCardapioUseCase.execute(10L, itemDto("Lasanha")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 10");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    void atualizarShouldPersistChangesWhenItemBelongsToRestaurante() {
        ItemCardapioRequestDTO dto = itemDto("Ravioli");
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findById(5L)).thenReturn(Optional.of(item(5L, 10L, "Lasanha")));
        when(itemCardapioRepository.update(any(ItemCardapio.class), eq(5L))).thenReturn(1);

        atualizarItemCardapioUseCase.execute(10L, 5L, dto);

        ArgumentCaptor<ItemCardapio> captor = ArgumentCaptor.forClass(ItemCardapio.class);
        verify(itemCardapioRepository).update(captor.capture(), eq(5L));
        ItemCardapio atualizado = captor.getValue();
        assertThat(atualizado.getRestauranteId()).isEqualTo(10L);
        assertThat(atualizado.getNome()).isEqualTo(dto.nome());
    }

    @Test
    void atualizarShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> atualizarItemCardapioUseCase.execute(10L, 1L, itemDto("Ravioli")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 10");

        verify(itemCardapioRepository, never()).findById(any());
    }

    @Test
    void atualizarShouldThrowWhenItemDoesNotExist() {
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarItemCardapioUseCase.execute(10L, 1L, itemDto("Ravioli")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Item do cardápio não encontrado com id: 1");

        verify(itemCardapioRepository, never()).update(any(), eq(1L));
    }

    @Test
    void atualizarShouldThrowWhenItemBelongsToDifferentRestaurante() {
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item(1L, 99L, "Lasanha")));

        assertThatThrownBy(() -> atualizarItemCardapioUseCase.execute(10L, 1L, itemDto("Ravioli")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Item do cardápio não encontrado para o restaurante 10");

        verify(itemCardapioRepository, never()).update(any(), eq(1L));
    }

    @Test
    void excluirShouldRemoveItemWhenItBelongsToRestaurante() {
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findById(5L)).thenReturn(Optional.of(item(5L, 10L, "Lasanha")));

        excluirItemCardapioUseCase.execute(10L, 5L);

        verify(itemCardapioRepository).delete(5L);
    }

    @Test
    void excluirShouldThrowWhenRestauranteDoesNotExist() {
        when(restauranteRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> excluirItemCardapioUseCase.execute(10L, 5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Restaurante não encontrado com id: 10");

        verify(itemCardapioRepository, never()).findById(any());
    }

    @Test
    void excluirShouldThrowWhenItemDoesNotExist() {
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> excluirItemCardapioUseCase.execute(10L, 5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Item do cardápio não encontrado com id: 5");

        verify(itemCardapioRepository, never()).delete(any());
    }

    @Test
    void excluirShouldThrowWhenItemBelongsToDifferentRestaurante() {
        when(restauranteRepository.existsById(10L)).thenReturn(true);
        when(itemCardapioRepository.findById(5L)).thenReturn(Optional.of(item(5L, 99L, "Lasanha")));

        assertThatThrownBy(() -> excluirItemCardapioUseCase.execute(10L, 5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Item do cardápio não encontrado para o restaurante 10");

        verify(itemCardapioRepository, never()).delete(any());
    }

    private ItemCardapioRequestDTO itemDto(String nome) {
        return new ItemCardapioRequestDTO(nome, "Massa recheada", new BigDecimal("35.90"), true, "/fotos/ravioli.jpg");
    }

    private ItemCardapio item(Long id, Long restauranteId, String nome) {
        return new ItemCardapio(id, restauranteId, nome, "Massa recheada", new BigDecimal("35.90"), true, "/fotos/ravioli.jpg");
    }
}
