package br.com.fiap.usuarios.infrastructure.web.controller;

import br.com.fiap.usuarios.application.dto.itemcardapio.ItemCardapioRequestDTO;
import br.com.fiap.usuarios.application.dto.itemcardapio.ItemCardapioResponseDTO;
import br.com.fiap.usuarios.application.mapper.ItemCardapioMapper;
import br.com.fiap.usuarios.application.usecase.itemcardapio.AtualizarItemCardapioUseCase;
import br.com.fiap.usuarios.application.usecase.itemcardapio.BuscarItemCardapioPorIdUseCase;
import br.com.fiap.usuarios.application.usecase.itemcardapio.CriarItemCardapioUseCase;
import br.com.fiap.usuarios.application.usecase.itemcardapio.ExcluirItemCardapioUseCase;
import br.com.fiap.usuarios.application.usecase.itemcardapio.ListarItensCardapioUseCase;
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
@RequestMapping("/v1/restaurantes/{restauranteId}/itens-cardapio")
@Tag(name = "Itens do Cardápio", description = "CRUD dos itens vendidos no cardápio de um restaurante")
public class ItemCardapioController {

    private static final Logger logger = LoggerFactory.getLogger(ItemCardapioController.class);

    private final ListarItensCardapioUseCase listarItensCardapioUseCase;
    private final BuscarItemCardapioPorIdUseCase buscarItemCardapioPorIdUseCase;
    private final CriarItemCardapioUseCase criarItemCardapioUseCase;
    private final AtualizarItemCardapioUseCase atualizarItemCardapioUseCase;
    private final ExcluirItemCardapioUseCase excluirItemCardapioUseCase;

    public ItemCardapioController(ListarItensCardapioUseCase listarItensCardapioUseCase,
                                   BuscarItemCardapioPorIdUseCase buscarItemCardapioPorIdUseCase,
                                   CriarItemCardapioUseCase criarItemCardapioUseCase,
                                   AtualizarItemCardapioUseCase atualizarItemCardapioUseCase,
                                   ExcluirItemCardapioUseCase excluirItemCardapioUseCase) {
        this.listarItensCardapioUseCase = listarItensCardapioUseCase;
        this.buscarItemCardapioPorIdUseCase = buscarItemCardapioPorIdUseCase;
        this.criarItemCardapioUseCase = criarItemCardapioUseCase;
        this.atualizarItemCardapioUseCase = atualizarItemCardapioUseCase;
        this.excluirItemCardapioUseCase = excluirItemCardapioUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar itens do cardápio de um restaurante, com paginação")
    public ResponseEntity<List<ItemCardapioResponseDTO>> findByRestauranteId(
            @PathVariable Long restauranteId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("GET /v1/restaurantes/{}/itens-cardapio - page={}, size={}", restauranteId, page, size);
        var itens = listarItensCardapioUseCase.execute(restauranteId, page, size).stream()
                .map(ItemCardapioMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item do cardápio por ID")
    public ResponseEntity<ItemCardapioResponseDTO> findById(@PathVariable Long restauranteId, @PathVariable Long id) {
        logger.info("GET /v1/restaurantes/{}/itens-cardapio/{}", restauranteId, id);
        var item = buscarItemCardapioPorIdUseCase.execute(id);
        return ResponseEntity.ok(ItemCardapioMapper.toResponseDTO(item));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo item do cardápio")
    public ResponseEntity<Void> save(@PathVariable Long restauranteId, @Valid @RequestBody ItemCardapioRequestDTO dto) {
        logger.info("POST /v1/restaurantes/{}/itens-cardapio", restauranteId);
        criarItemCardapioUseCase.execute(restauranteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item do cardápio")
    public ResponseEntity<Void> update(@PathVariable Long restauranteId, @PathVariable Long id, @Valid @RequestBody ItemCardapioRequestDTO dto) {
        logger.info("PUT /v1/restaurantes/{}/itens-cardapio/{}", restauranteId, id);
        atualizarItemCardapioUseCase.execute(restauranteId, id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir item do cardápio")
    public ResponseEntity<Void> delete(@PathVariable Long restauranteId, @PathVariable Long id) {
        logger.info("DELETE /v1/restaurantes/{}/itens-cardapio/{}", restauranteId, id);
        excluirItemCardapioUseCase.execute(restauranteId, id);
        return ResponseEntity.noContent().build();
    }
}
