package br.com.fiap.techchallenge.application.usecase.tipousuario;

import br.com.fiap.techchallenge.application.dto.tipousuario.TipoUsuarioRequestDTO;
import br.com.fiap.techchallenge.domain.exception.BusinessException;
import br.com.fiap.techchallenge.domain.exception.ResourceNotFoundException;
import br.com.fiap.techchallenge.domain.model.TipoUsuario;
import br.com.fiap.techchallenge.domain.repository.TipoUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoUsuarioUseCasesTest {

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    private ListarTiposUsuarioUseCase listarTiposUsuarioUseCase;
    private BuscarTipoUsuarioPorIdUseCase buscarTipoUsuarioPorIdUseCase;
    private CriarTipoUsuarioUseCase criarTipoUsuarioUseCase;
    private AtualizarTipoUsuarioUseCase atualizarTipoUsuarioUseCase;
    private ExcluirTipoUsuarioUseCase excluirTipoUsuarioUseCase;

    @BeforeEach
    void setUp() {
        listarTiposUsuarioUseCase = new ListarTiposUsuarioUseCase(tipoUsuarioRepository);
        buscarTipoUsuarioPorIdUseCase = new BuscarTipoUsuarioPorIdUseCase(tipoUsuarioRepository);
        criarTipoUsuarioUseCase = new CriarTipoUsuarioUseCase(tipoUsuarioRepository);
        atualizarTipoUsuarioUseCase = new AtualizarTipoUsuarioUseCase(tipoUsuarioRepository);
        excluirTipoUsuarioUseCase = new ExcluirTipoUsuarioUseCase(tipoUsuarioRepository);
    }

    @Test
    void listarShouldReturnRepositoryData() {
        List<TipoUsuario> tipos = List.of(new TipoUsuario(1L, "CLIENTE"));
        when(tipoUsuarioRepository.findAll()).thenReturn(tipos);

        assertThat(listarTiposUsuarioUseCase.execute()).isEqualTo(tipos);
    }

    @Test
    void buscarPorIdShouldReturnTipoUsuarioWhenFound() {
        TipoUsuario tipoUsuario = new TipoUsuario(1L, "CLIENTE");
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));

        assertThat(buscarTipoUsuarioPorIdUseCase.execute(1L)).isEqualTo(tipoUsuario);
    }

    @Test
    void buscarPorIdShouldThrowWhenTipoUsuarioDoesNotExist() {
        when(tipoUsuarioRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarTipoUsuarioPorIdUseCase.execute(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de usuário não encontrado com id: 9");
    }

    @Test
    void criarShouldPersistTipoUsuarioWhenNomeIsUnique() {
        TipoUsuarioRequestDTO dto = new TipoUsuarioRequestDTO("ENTREGADOR");
        when(tipoUsuarioRepository.findByNome(dto.nome())).thenReturn(Optional.empty());
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(1);

        criarTipoUsuarioUseCase.execute(dto);

        ArgumentCaptor<TipoUsuario> captor = ArgumentCaptor.forClass(TipoUsuario.class);
        verify(tipoUsuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo(dto.nome());
    }

    @Test
    void criarShouldThrowWhenNomeAlreadyExists() {
        TipoUsuarioRequestDTO dto = new TipoUsuarioRequestDTO("CLIENTE");
        when(tipoUsuarioRepository.findByNome(dto.nome())).thenReturn(Optional.of(new TipoUsuario(1L, "CLIENTE")));

        assertThatThrownBy(() -> criarTipoUsuarioUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Tipo de usuário já cadastrado: CLIENTE");

        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    void atualizarShouldPersistChangesWhenTipoUsuarioExistsAndNomeIsAvailable() {
        TipoUsuarioRequestDTO dto = new TipoUsuarioRequestDTO("GERENTE");
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(new TipoUsuario(1L, "CLIENTE")));
        when(tipoUsuarioRepository.findByNome(dto.nome())).thenReturn(Optional.empty());
        when(tipoUsuarioRepository.update(any(TipoUsuario.class), eq(1L))).thenReturn(1);

        atualizarTipoUsuarioUseCase.execute(1L, dto);

        ArgumentCaptor<TipoUsuario> captor = ArgumentCaptor.forClass(TipoUsuario.class);
        verify(tipoUsuarioRepository).update(captor.capture(), eq(1L));
        assertThat(captor.getValue().getNome()).isEqualTo(dto.nome());
    }

    @Test
    void atualizarShouldThrowWhenTipoUsuarioDoesNotExist() {
        when(tipoUsuarioRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarTipoUsuarioUseCase.execute(7L, new TipoUsuarioRequestDTO("GERENTE")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de usuário não encontrado com id: 7");
    }

    @Test
    void atualizarShouldThrowWhenNomeBelongsToAnotherTipoUsuario() {
        TipoUsuarioRequestDTO dto = new TipoUsuarioRequestDTO("CLIENTE");
        when(tipoUsuarioRepository.findById(2L)).thenReturn(Optional.of(new TipoUsuario(2L, "DONO_RESTAURANTE")));
        when(tipoUsuarioRepository.findByNome(dto.nome())).thenReturn(Optional.of(new TipoUsuario(1L, "CLIENTE")));

        assertThatThrownBy(() -> atualizarTipoUsuarioUseCase.execute(2L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Tipo de usuário já cadastrado: CLIENTE");

        verify(tipoUsuarioRepository, never()).update(any(), eq(2L));
    }

    @Test
    void atualizarShouldNotFailWhenDuplicateNomeBelongsToSameTipoUsuario() {
        TipoUsuarioRequestDTO dto = new TipoUsuarioRequestDTO("CLIENTE");
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(new TipoUsuario(1L, "CLIENTE")));
        when(tipoUsuarioRepository.findByNome(dto.nome())).thenReturn(Optional.of(new TipoUsuario(1L, "CLIENTE")));
        when(tipoUsuarioRepository.update(any(TipoUsuario.class), eq(1L))).thenReturn(1);

        assertThatCode(() -> atualizarTipoUsuarioUseCase.execute(1L, dto)).doesNotThrowAnyException();
        verify(tipoUsuarioRepository).update(any(TipoUsuario.class), eq(1L));
    }

    @Test
    void excluirShouldRemoveTipoUsuarioWhenThereAreNoAssociatedUsuarios() {
        when(tipoUsuarioRepository.findById(5L)).thenReturn(Optional.of(new TipoUsuario(5L, "GERENTE")));
        when(tipoUsuarioRepository.existsUsuariosComTipo(5L)).thenReturn(false);

        excluirTipoUsuarioUseCase.execute(5L);

        verify(tipoUsuarioRepository).delete(5L);
    }

    @Test
    void excluirShouldThrowWhenTipoUsuarioDoesNotExist() {
        when(tipoUsuarioRepository.findById(11L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> excluirTipoUsuarioUseCase.execute(11L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de usuário não encontrado com id: 11");

        verify(tipoUsuarioRepository, never()).delete(any());
    }

    @Test
    void excluirShouldThrowWhenThereAreAssociatedUsuarios() {
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(new TipoUsuario(1L, "CLIENTE")));
        when(tipoUsuarioRepository.existsUsuariosComTipo(1L)).thenReturn(true);

        assertThatThrownBy(() -> excluirTipoUsuarioUseCase.execute(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível excluir: existem usuários associados a este tipo");

        verify(tipoUsuarioRepository, never()).delete(any());
    }
}
