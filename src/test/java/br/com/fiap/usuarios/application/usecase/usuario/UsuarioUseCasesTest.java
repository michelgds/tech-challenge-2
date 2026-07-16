package br.com.fiap.usuarios.application.usecase.usuario;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;
import br.com.fiap.usuarios.application.dto.usuario.LoginRequestDTO;
import br.com.fiap.usuarios.application.dto.usuario.SenhaUpdateDTO;
import br.com.fiap.usuarios.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.usuarios.application.dto.usuario.UsuarioUpdateDTO;
import br.com.fiap.usuarios.domain.exception.AuthenticationException;
import br.com.fiap.usuarios.domain.exception.BusinessException;
import br.com.fiap.usuarios.domain.exception.ResourceNotFoundException;
import br.com.fiap.usuarios.domain.model.Endereco;
import br.com.fiap.usuarios.domain.model.TipoUsuario;
import br.com.fiap.usuarios.domain.model.Usuario;
import br.com.fiap.usuarios.domain.repository.TipoUsuarioRepository;
import br.com.fiap.usuarios.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa os casos de uso de usuário (um por classe/responsabilidade),
 * substituindo o antigo UsuarioService monolítico.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioUseCasesTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ListarUsuariosUseCase listarUsuariosUseCase;
    private BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private CriarUsuarioUseCase criarUsuarioUseCase;
    private AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private AtualizarSenhaUsuarioUseCase atualizarSenhaUsuarioUseCase;
    private ExcluirUsuarioUseCase excluirUsuarioUseCase;
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @BeforeEach
    void setUp() {
        listarUsuariosUseCase = new ListarUsuariosUseCase(usuarioRepository);
        buscarUsuarioPorIdUseCase = new BuscarUsuarioPorIdUseCase(usuarioRepository);
        criarUsuarioUseCase = new CriarUsuarioUseCase(usuarioRepository, tipoUsuarioRepository, passwordEncoder);
        atualizarUsuarioUseCase = new AtualizarUsuarioUseCase(usuarioRepository, tipoUsuarioRepository);
        atualizarSenhaUsuarioUseCase = new AtualizarSenhaUsuarioUseCase(usuarioRepository, passwordEncoder);
        excluirUsuarioUseCase = new ExcluirUsuarioUseCase(usuarioRepository);
        autenticarUsuarioUseCase = new AutenticarUsuarioUseCase(usuarioRepository, passwordEncoder);
    }

    @Test
    void listarShouldDelegateUsingCalculatedOffsetWhenNomeIsAbsent() {
        List<Usuario> usuarios = List.of(usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L));
        when(usuarioRepository.findAll(5, 10)).thenReturn(usuarios);

        List<Usuario> result = listarUsuariosUseCase.execute(3, 5, null);

        assertThat(result).isEqualTo(usuarios);
        verify(usuarioRepository).findAll(5, 10);
    }

    @Test
    void listarShouldDelegateToFindByNomeWhenNomeIsPresent() {
        List<Usuario> usuarios = List.of(usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L));
        when(usuarioRepository.findByNome("Mar")).thenReturn(usuarios);

        List<Usuario> result = listarUsuariosUseCase.execute(1, 10, "Mar");

        assertThat(result).isEqualTo(usuarios);
        verify(usuarioRepository).findByNome("Mar");
        verify(usuarioRepository, never()).findAll(any(Integer.class), any(Integer.class));
    }

    @Test
    void buscarPorIdShouldReturnUsuarioWhenFound() {
        Usuario usuario = usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThat(buscarUsuarioPorIdUseCase.execute(1L)).isEqualTo(usuario);
    }

    @Test
    void buscarPorIdShouldThrowWhenUsuarioDoesNotExist() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarUsuarioPorIdUseCase.execute(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com id: 99");
    }

    @Test
    void criarShouldPersistUsuarioComSenhaCriptografadaQuandoDadosValidos() {
        UsuarioRequestDTO dto = usuarioRequestDto();
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(tipoUsuarioRepository.findById(dto.tipoUsuarioId())).thenReturn(Optional.of(new TipoUsuario(2L, "CLIENTE")));
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash-da-senha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(1);

        criarUsuarioUseCase.execute(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getNome()).isEqualTo(dto.nome());
        assertThat(salvo.getEmail()).isEqualTo(dto.email());
        assertThat(salvo.getLogin()).isEqualTo(dto.login());
        assertThat(salvo.getSenha()).isEqualTo("hash-da-senha");
        assertThat(salvo.getTipoUsuarioId()).isEqualTo(dto.tipoUsuarioId());
        assertThat(salvo.getDataUltimaAlteracao()).isNotNull();
        assertThat(salvo.getEndereco()).isEqualTo(new Endereco("Rua A", "10", "São Paulo", "SP", "01000-000"));
    }

    @Test
    void criarShouldThrowWhenEmailAlreadyExists() {
        UsuarioRequestDTO dto = usuarioRequestDto();
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuario(5L, "Outro", dto.email(), "outro", "hash", 2L)));

        assertThatThrownBy(() -> criarUsuarioUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-mail já cadastrado: " + dto.email());

        verify(tipoUsuarioRepository, never()).findById(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarShouldThrowWhenTipoUsuarioDoesNotExist() {
        UsuarioRequestDTO dto = usuarioRequestDto();
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(tipoUsuarioRepository.findById(dto.tipoUsuarioId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> criarUsuarioUseCase.execute(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de usuário não encontrado com id: " + dto.tipoUsuarioId());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizarShouldPersistChangesWhenDataIsValid() {
        UsuarioUpdateDTO dto = usuarioUpdateDto("maria.nova@email.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L)));
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(tipoUsuarioRepository.findById(dto.tipoUsuarioId())).thenReturn(Optional.of(new TipoUsuario(2L, "CLIENTE")));
        when(usuarioRepository.update(any(Usuario.class), eq(1L))).thenReturn(1);

        atualizarUsuarioUseCase.execute(1L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).update(captor.capture(), eq(1L));
        Usuario atualizado = captor.getValue();
        assertThat(atualizado.getNome()).isEqualTo(dto.nome());
        assertThat(atualizado.getEmail()).isEqualTo(dto.email());
        assertThat(atualizado.getLogin()).isEqualTo(dto.login());
        assertThat(atualizado.getTipoUsuarioId()).isEqualTo(dto.tipoUsuarioId());
        assertThat(atualizado.getSenha()).isNull();
        assertThat(atualizado.getEndereco()).isEqualTo(new Endereco("Rua Nova", "20", "Campinas", "SP", "13000-000"));
    }

    @Test
    void atualizarShouldThrowWhenUsuarioDoesNotExist() {
        when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarUsuarioUseCase.execute(77L, usuarioUpdateDto("maria.nova@email.com")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com id: 77");
    }

    @Test
    void atualizarShouldThrowWhenEmailBelongsToAnotherUsuario() {
        UsuarioUpdateDTO dto = usuarioUpdateDto("duplicado@email.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L)));
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuario(2L, "Outro", dto.email(), "outro", "hash", 2L)));

        assertThatThrownBy(() -> atualizarUsuarioUseCase.execute(1L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-mail já cadastrado: " + dto.email());

        verify(tipoUsuarioRepository, never()).findById(any());
    }

    @Test
    void atualizarShouldThrowWhenTipoUsuarioDoesNotExist() {
        UsuarioUpdateDTO dto = usuarioUpdateDto("maria.nova@email.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L)));
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(tipoUsuarioRepository.findById(dto.tipoUsuarioId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarUsuarioUseCase.execute(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de usuário não encontrado com id: " + dto.tipoUsuarioId());

        verify(usuarioRepository, never()).update(any(), eq(1L));
    }

    @Test
    void atualizarSenhaShouldPersistNewSenhaHashWhenCurrentSenhaMatches() {
        Usuario usuarioExistente = usuario(1L, "Maria", "maria@email.com", "maria", "hash-atual", 2L);
        SenhaUpdateDTO dto = new SenhaUpdateDTO("senhaAtual", "novaSenha");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.matches("senhaAtual", "hash-atual")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("hash-nova");
        when(usuarioRepository.updateSenha(1L, "hash-nova")).thenReturn(1);

        atualizarSenhaUsuarioUseCase.execute(1L, dto);

        verify(usuarioRepository).updateSenha(1L, "hash-nova");
    }

    @Test
    void atualizarSenhaShouldThrowWhenCurrentSenhaIsWrong() {
        Usuario usuarioExistente = usuario(1L, "Maria", "maria@email.com", "maria", "hash-atual", 2L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.matches("errada", "hash-atual")).thenReturn(false);

        assertThatThrownBy(() -> atualizarSenhaUsuarioUseCase.execute(1L, new SenhaUpdateDTO("errada", "novaSenha")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Senha atual incorreta");

        verify(usuarioRepository, never()).updateSenha(any(), any());
    }

    @Test
    void atualizarSenhaShouldThrowWhenUsuarioDoesNotExist() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarSenhaUsuarioUseCase.execute(10L, new SenhaUpdateDTO("senha", "nova")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com id: 10");
    }

    @Test
    void excluirShouldRemoveUsuarioWhenItExists() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L)));

        excluirUsuarioUseCase.execute(1L);

        verify(usuarioRepository).delete(1L);
    }

    @Test
    void excluirShouldThrowWhenUsuarioDoesNotExist() {
        when(usuarioRepository.findById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> excluirUsuarioUseCase.execute(15L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com id: 15");

        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    void autenticarShouldReturnUsuarioWhenCredentialsAreValid() {
        Usuario usuario = usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L);
        LoginRequestDTO dto = new LoginRequestDTO("maria", "senha");
        when(usuarioRepository.findByLogin("maria")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);

        Usuario result = autenticarUsuarioUseCase.execute(dto);

        assertThat(result).isEqualTo(usuario);
    }

    @Test
    void autenticarShouldThrowWhenSenhaIsInvalid() {
        Usuario usuario = usuario(1L, "Maria", "maria@email.com", "maria", "hash", 2L);
        LoginRequestDTO dto = new LoginRequestDTO("maria", "senhaErrada");
        when(usuarioRepository.findByLogin("maria")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hash")).thenReturn(false);

        assertThatThrownBy(() -> autenticarUsuarioUseCase.execute(dto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Login ou senha inválidos");
    }

    @Test
    void autenticarShouldThrowWhenLoginDoesNotExist() {
        LoginRequestDTO dto = new LoginRequestDTO("desconhecido", "senha");
        when(usuarioRepository.findByLogin("desconhecido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autenticarUsuarioUseCase.execute(dto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Login ou senha inválidos");
    }

    private UsuarioRequestDTO usuarioRequestDto() {
        return new UsuarioRequestDTO(
                "Maria",
                "maria@email.com",
                "maria",
                "senha123",
                2L,
                new EnderecoDTO("Rua A", "10", "São Paulo", "SP", "01000-000")
        );
    }

    private UsuarioUpdateDTO usuarioUpdateDto(String email) {
        return new UsuarioUpdateDTO(
                "Maria Atualizada",
                email,
                "maria.nova",
                2L,
                new EnderecoDTO("Rua Nova", "20", "Campinas", "SP", "13000-000")
        );
    }

    private Usuario usuario(Long id, String nome, String email, String login, String senha, Long tipoUsuarioId) {
        return new Usuario(id, nome, email, login, senha, tipoUsuarioId, "CLIENTE", null,
                new Endereco("Rua A", "10", "São Paulo", "SP", "01000-000"));
    }
}
