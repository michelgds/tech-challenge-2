package br.com.fiap.techchallenge.infrastructure.web.controller;

import br.com.fiap.techchallenge.application.dto.common.EnderecoDTO;
import br.com.fiap.techchallenge.application.dto.itemcardapio.ItemCardapioRequestDTO;
import br.com.fiap.techchallenge.application.dto.restaurante.RestauranteRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemCardapioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteItemCardapioCrudFlow() throws Exception {
        long adminId = findUsuarioIdByEmail("Admin", "admin@restaurante.com");
        long restauranteId = createRestaurante("Restaurante Item " + uniqueSuffix(), adminId);
        String itemName = "Item " + uniqueSuffix();
        ItemCardapioRequestDTO createDto = new ItemCardapioRequestDTO(
                itemName,
                "Descrição inicial",
                new BigDecimal("29.90"),
                false,
                "/fotos/item.jpg"
        );

        mockMvc.perform(post("/v1/restaurantes/{restauranteId}/itens-cardapio", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        JsonNode created = findItemByName(restauranteId, itemName);
        long itemId = created.path("id").asLong();

        mockMvc.perform(get("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.nome").value(itemName));

        ItemCardapioRequestDTO updateDto = new ItemCardapioRequestDTO(
                itemName + " Atualizado",
                "Descrição final",
                new BigDecimal("39.90"),
                true,
                "/fotos/item-atualizado.jpg"
        );

        mockMvc.perform(put("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(updateDto.nome()))
                .andExpect(jsonPath("$.disponivelSomenteLocal").value(true));

        mockMvc.perform(delete("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteId, itemId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteId, itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item do cardápio não encontrado com id: " + itemId));

        mockMvc.perform(delete("/v1/restaurantes/{id}", restauranteId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenRestauranteDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/restaurantes/{restauranteId}/itens-cardapio", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Restaurante não encontrado com id: " + Long.MAX_VALUE));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidItemPayload() throws Exception {
        long adminId = findUsuarioIdByEmail("Admin", "admin@restaurante.com");
        long restauranteId = createRestaurante("Restaurante Validação " + uniqueSuffix(), adminId);
        ItemCardapioRequestDTO dto = new ItemCardapioRequestDTO("", "Descrição", BigDecimal.ZERO, null, null);

        mockMvc.perform(post("/v1/restaurantes/{restauranteId}/itens-cardapio", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));

        mockMvc.perform(delete("/v1/restaurantes/{id}", restauranteId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenItemBelongsToDifferentRestaurante() throws Exception {
        long adminId = findUsuarioIdByEmail("Admin", "admin@restaurante.com");
        long restauranteA = createRestaurante("Restaurante A " + uniqueSuffix(), adminId);
        long restauranteB = createRestaurante("Restaurante B " + uniqueSuffix(), adminId);
        String itemName = "Item Cruzado " + uniqueSuffix();

        mockMvc.perform(post("/v1/restaurantes/{restauranteId}/itens-cardapio", restauranteA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemCardapioRequestDTO(
                                itemName,
                                "Descrição",
                                new BigDecimal("19.90"),
                                false,
                                "/fotos/cruzado.jpg"))))
                .andExpect(status().isCreated());

        long itemId = findItemByName(restauranteA, itemName).path("id").asLong();

        mockMvc.perform(put("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteB, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemCardapioRequestDTO(
                                itemName + " Atualizado",
                                "Descrição",
                                new BigDecimal("21.90"),
                                true,
                                "/fotos/cruzado-atualizado.jpg"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item do cardápio não encontrado para o restaurante " + restauranteB));

        mockMvc.perform(delete("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteB, itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item do cardápio não encontrado para o restaurante " + restauranteB));

        mockMvc.perform(delete("/v1/restaurantes/{restauranteId}/itens-cardapio/{id}", restauranteA, itemId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/v1/restaurantes/{id}", restauranteA))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/v1/restaurantes/{id}", restauranteB))
                .andExpect(status().isNoContent());
    }

    private long findUsuarioIdByEmail(String nome, String email) throws Exception {
        JsonNode usuarios = readJsonArray(mockMvc.perform(get("/v1/usuarios").param("nome", nome))
                .andExpect(status().isOk())
                .andReturn());
        for (JsonNode usuario : usuarios) {
            if (email.equals(usuario.path("email").asText())) {
                return usuario.path("id").asLong();
            }
        }
        throw new AssertionError("Usuário não encontrado: " + email);
    }

    private long createRestaurante(String nome, long adminId) throws Exception {
        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RestauranteRequestDTO(
                                nome,
                                new EnderecoDTO("Rua Teste", "1", "São Paulo", "SP", "01000-000"),
                                "Variada",
                                "10h às 22h",
                                adminId))))
                .andExpect(status().isCreated());
        return findRestauranteByName(nome).path("id").asLong();
    }

    private JsonNode findRestauranteByName(String nome) throws Exception {
        JsonNode restaurantes = readJsonArray(mockMvc.perform(get("/v1/restaurantes").param("nome", nome))
                .andExpect(status().isOk())
                .andReturn());
        for (JsonNode restaurante : restaurantes) {
            if (nome.equals(restaurante.path("nome").asText())) {
                return restaurante;
            }
        }
        throw new AssertionError("Restaurante não encontrado: " + nome);
    }

    private JsonNode findItemByName(long restauranteId, String nome) throws Exception {
        JsonNode itens = readJsonArray(mockMvc.perform(get("/v1/restaurantes/{restauranteId}/itens-cardapio", restauranteId))
                .andExpect(status().isOk())
                .andReturn());
        for (JsonNode item : itens) {
            if (nome.equals(item.path("nome").asText())) {
                return item;
            }
        }
        throw new AssertionError("Item não encontrado: " + nome);
    }

    private JsonNode readJsonArray(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
