package br.com.fiap.techchallenge.infrastructure.web.controller;

import br.com.fiap.techchallenge.application.dto.tipousuario.TipoUsuarioRequestDTO;
import br.com.fiap.techchallenge.application.dto.tipousuario.TipoUsuarioResponseDTO;
import br.com.fiap.techchallenge.application.mapper.TipoUsuarioMapper;
import br.com.fiap.techchallenge.application.usecase.tipousuario.AtualizarTipoUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.tipousuario.BuscarTipoUsuarioPorIdUseCase;
import br.com.fiap.techchallenge.application.usecase.tipousuario.CriarTipoUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.tipousuario.ExcluirTipoUsuarioUseCase;
import br.com.fiap.techchallenge.application.usecase.tipousuario.ListarTiposUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tipos-usuario")
@Tag(name = "Tipos de Usuário", description = "CRUD de tipos de usuário (ex: DONO_RESTAURANTE, CLIENTE)")
public class TipoUsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(TipoUsuarioController.class);

    private final ListarTiposUsuarioUseCase listarTiposUsuarioUseCase;
    private final BuscarTipoUsuarioPorIdUseCase buscarTipoUsuarioPorIdUseCase;
    private final CriarTipoUsuarioUseCase criarTipoUsuarioUseCase;
    private final AtualizarTipoUsuarioUseCase atualizarTipoUsuarioUseCase;
    private final ExcluirTipoUsuarioUseCase excluirTipoUsuarioUseCase;

    public TipoUsuarioController(ListarTiposUsuarioUseCase listarTiposUsuarioUseCase,
                                  BuscarTipoUsuarioPorIdUseCase buscarTipoUsuarioPorIdUseCase,
                                  CriarTipoUsuarioUseCase criarTipoUsuarioUseCase,
                                  AtualizarTipoUsuarioUseCase atualizarTipoUsuarioUseCase,
                                  ExcluirTipoUsuarioUseCase excluirTipoUsuarioUseCase) {
        this.listarTiposUsuarioUseCase = listarTiposUsuarioUseCase;
        this.buscarTipoUsuarioPorIdUseCase = buscarTipoUsuarioPorIdUseCase;
        this.criarTipoUsuarioUseCase = criarTipoUsuarioUseCase;
        this.atualizarTipoUsuarioUseCase = atualizarTipoUsuarioUseCase;
        this.excluirTipoUsuarioUseCase = excluirTipoUsuarioUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar todos os tipos de usuário")
    public ResponseEntity<List<TipoUsuarioResponseDTO>> findAll() {
        logger.info("GET /v1/tipos-usuario");
        var tipos = listarTiposUsuarioUseCase.execute().stream()
                .map(TipoUsuarioMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de usuário por ID")
    public ResponseEntity<TipoUsuarioResponseDTO> findById(@PathVariable Long id) {
        logger.info("GET /v1/tipos-usuario/{}", id);
        var tipoUsuario = buscarTipoUsuarioPorIdUseCase.execute(id);
        return ResponseEntity.ok(TipoUsuarioMapper.toResponseDTO(tipoUsuario));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo tipo de usuário")
    public ResponseEntity<Void> save(@Valid @RequestBody TipoUsuarioRequestDTO dto) {
        logger.info("POST /v1/tipos-usuario");
        criarTipoUsuarioUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tipo de usuário")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody TipoUsuarioRequestDTO dto) {
        logger.info("PUT /v1/tipos-usuario/{}", id);
        atualizarTipoUsuarioUseCase.execute(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tipo de usuário")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /v1/tipos-usuario/{}", id);
        excluirTipoUsuarioUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
