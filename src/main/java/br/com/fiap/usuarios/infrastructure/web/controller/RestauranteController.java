package br.com.fiap.usuarios.infrastructure.web.controller;

import br.com.fiap.usuarios.application.dto.restaurante.RestauranteRequestDTO;
import br.com.fiap.usuarios.application.dto.restaurante.RestauranteResponseDTO;
import br.com.fiap.usuarios.application.mapper.RestauranteMapper;
import br.com.fiap.usuarios.application.usecase.restaurante.AtualizarRestauranteUseCase;
import br.com.fiap.usuarios.application.usecase.restaurante.BuscarRestaurantePorIdUseCase;
import br.com.fiap.usuarios.application.usecase.restaurante.CriarRestauranteUseCase;
import br.com.fiap.usuarios.application.usecase.restaurante.ExcluirRestauranteUseCase;
import br.com.fiap.usuarios.application.usecase.restaurante.ListarRestaurantesUseCase;
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
@RequestMapping("/v1/restaurantes")
@Tag(name = "Restaurantes", description = "CRUD de restaurantes")
public class RestauranteController {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteController.class);

    private final ListarRestaurantesUseCase listarRestaurantesUseCase;
    private final BuscarRestaurantePorIdUseCase buscarRestaurantePorIdUseCase;
    private final CriarRestauranteUseCase criarRestauranteUseCase;
    private final AtualizarRestauranteUseCase atualizarRestauranteUseCase;
    private final ExcluirRestauranteUseCase excluirRestauranteUseCase;

    public RestauranteController(ListarRestaurantesUseCase listarRestaurantesUseCase,
                                  BuscarRestaurantePorIdUseCase buscarRestaurantePorIdUseCase,
                                  CriarRestauranteUseCase criarRestauranteUseCase,
                                  AtualizarRestauranteUseCase atualizarRestauranteUseCase,
                                  ExcluirRestauranteUseCase excluirRestauranteUseCase) {
        this.listarRestaurantesUseCase = listarRestaurantesUseCase;
        this.buscarRestaurantePorIdUseCase = buscarRestaurantePorIdUseCase;
        this.criarRestauranteUseCase = criarRestauranteUseCase;
        this.atualizarRestauranteUseCase = atualizarRestauranteUseCase;
        this.excluirRestauranteUseCase = excluirRestauranteUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar restaurantes com paginação e filtro opcional por nome")
    public ResponseEntity<List<RestauranteResponseDTO>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome
    ) {
        logger.info("GET /v1/restaurantes - page={}, size={}, nome={}", page, size, nome);
        var restaurantes = listarRestaurantesUseCase.execute(page, size, nome).stream()
                .map(RestauranteMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID")
    public ResponseEntity<RestauranteResponseDTO> findById(@PathVariable Long id) {
        logger.info("GET /v1/restaurantes/{}", id);
        var restaurante = buscarRestaurantePorIdUseCase.execute(id);
        return ResponseEntity.ok(RestauranteMapper.toResponseDTO(restaurante));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo restaurante")
    public ResponseEntity<Void> save(@Valid @RequestBody RestauranteRequestDTO dto) {
        logger.info("POST /v1/restaurantes");
        criarRestauranteUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar restaurante")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody RestauranteRequestDTO dto) {
        logger.info("PUT /v1/restaurantes/{}", id);
        atualizarRestauranteUseCase.execute(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir restaurante")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /v1/restaurantes/{}", id);
        excluirRestauranteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
