package br.com.fiap.techchallenge.infrastructure.web.controller;

import br.com.fiap.techchallenge.application.dto.usuario.LoginRequestDTO;
import br.com.fiap.techchallenge.application.dto.usuario.SenhaUpdateDTO;
import br.com.fiap.techchallenge.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.techchallenge.application.dto.usuario.UsuarioResponseDTO;
import br.com.fiap.techchallenge.application.dto.usuario.UsuarioUpdateDTO;
import br.com.fiap.techchallenge.application.mapper.UsuarioMapper;
import br.com.fiap.techchallenge.application.usecase.usuario.AtualizarSenhaUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.usuario.AutenticarUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.usuario.BuscarUsuarioPorIdUseCase;
import br.com.fiap.techchallenge.application.usecase.usuario.CriarUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.usuario.ListarUsuariosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários (Donos de Restaurante e Clientes)")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final AtualizarSenhaUsuarioUseCase atualizarSenhaUsuarioUseCase;
    private final ExcluirUsuarioUseCase excluirUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public UsuarioController(ListarUsuariosUseCase listarUsuariosUseCase,
                              BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase,
                              CriarUsuarioUseCase criarUsuarioUseCase,
                              AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                              AtualizarSenhaUsuarioUseCase atualizarSenhaUsuarioUseCase,
                              ExcluirUsuarioUseCase excluirUsuarioUseCase,
                              AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.buscarUsuarioPorIdUseCase = buscarUsuarioPorIdUseCase;
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.atualizarSenhaUsuarioUseCase = atualizarSenhaUsuarioUseCase;
        this.excluirUsuarioUseCase = excluirUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar usuários com paginação e filtro opcional por nome")
    public ResponseEntity<List<UsuarioResponseDTO>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome
    ) {
        logger.info("GET /v1/usuarios - page={}, size={}, nome={}", page, size, nome);
        var usuarios = listarUsuariosUseCase.execute(page, size, nome).stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        logger.info("GET /v1/usuarios/{}", id);
        var usuario = buscarUsuarioPorIdUseCase.execute(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(usuario));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nome": "João Silva",
                              "email": "joao@email.com",
                              "login": "joao123",
                              "senha": "senha123",
                              "tipoUsuarioId": 2,
                              "endereco": {
                                "rua": "Rua das Flores",
                                "numero": "123",
                                "cidade": "São Paulo",
                                "estado": "SP",
                                "cep": "01310-100"
                              }
                            }
                            """))
            ))
    public ResponseEntity<Void> save(@Valid @RequestBody UsuarioRequestDTO dto) {
        logger.info("POST /v1/usuarios");
        criarUsuarioUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

     @PutMapping("/{id}")
     @Operation(summary = "Atualizar informações do usuário (exceto senha)",
             description = "Atualiza nome, email, login, tipo e endereço. ⚠️ A senha NÃO pode ser alterada por este endpoint. Use PATCH /v1/usuarios/{id}/senha para alterar a senha.",
             requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                     content = @Content(examples = @ExampleObject(value = """
                             {
                               "nome": "João Silva Atualizado",
                               "email": "joao.novo@email.com",
                               "login": "joao123",
                               "tipoUsuarioId": 2,
                               "endereco": {
                                 "rua": "Avenida Paulista",
                                 "numero": "1000",
                                 "cidade": "São Paulo",
                                 "estado": "SP",
                                 "cep": "01311-100"
                               }
                             }
                             """))
             ))
     public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
         logger.info("PUT /v1/usuarios/{}", id);
         atualizarUsuarioUseCase.execute(id, dto);
         return ResponseEntity.noContent().build();
     }

     @PatchMapping("/{id}/senha")
     @Operation(summary = "Trocar senha do usuário",
             description = "Endpoint exclusivo para alteração de senha. Requer validação da senha atual.")
     public ResponseEntity<Void> updateSenha(@PathVariable Long id, @Valid @RequestBody SenhaUpdateDTO dto) {
         logger.info("PATCH /v1/usuarios/{}/senha", id);
         atualizarSenhaUsuarioUseCase.execute(id, dto);
         return ResponseEntity.noContent().build();
     }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /v1/usuarios/{}", id);
        excluirUsuarioUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Validar login e senha do usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "login": "admin",
                              "senha": "admin123"
                            }
                            """))
            ))
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        logger.info("POST /v1/usuarios/login");
        var usuario = autenticarUsuarioUseCase.execute(dto);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(usuario));
    }
}
