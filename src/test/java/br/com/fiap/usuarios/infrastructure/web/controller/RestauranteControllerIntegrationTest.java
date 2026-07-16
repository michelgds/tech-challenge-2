package br.com.fiap.usuarios.infrastructure.web.controller;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;
import br.com.fiap.usuarios.application.dto.restaurante.RestauranteRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestauranteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteRestauranteCrudFlow() throws Exception {
        String suffix = uniqueSuffix();
        long donoId = findUsuarioIdByEmail("Admin", "admin@restaurante.com");
        RestauranteRequestDTO createDto = new RestauranteRequestDTO(
                "Restaurante " + suffix,
                new EnderecoDTO("Rua Itália", "50", "São Paulo", "SP", "01310-300"),
                "Italiana",
                "Seg a Sex 11h às 22h",
                donoId
        );

        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        JsonNode created = findRestauranteByName(createDto.nome());
        long restauranteId = created.path("id").asLong();

        mockMvc.perform(get("/v1/restaurantes/{id}", restauranteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restauranteId));

        RestauranteRequestDTO updateDto = new RestauranteRequestDTO(
                "Restaurante Atualizado " + suffix,
                new EnderecoDTO("Av. Paulista", "1000", "São Paulo", "SP", "01311-000"),
                "Contemporânea",
                "Todos os dias 10h às 23h",
                donoId
        );

        mockMvc.perform(put("/v1/restaurantes/{id}", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        JsonNode updated = findRestauranteByName(updateDto.nome());
        assertThat(updated.path("tipoCozinha").asText()).isEqualTo("Contemporânea");
        assertThat(updated.path("endereco").path("rua").asText()).isEqualTo("Av. Paulista");

        mockMvc.perform(delete("/v1/restaurantes/{id}", restauranteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/restaurantes/{id}", restauranteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    void shouldReturnNotFoundWhenDonoDoesNotExist() throws Exception {
        RestauranteRequestDTO dto = new RestauranteRequestDTO(
                "Restaurante Sem Dono " + uniqueSuffix(),
                new EnderecoDTO("Rua", "1", "São Paulo", "SP", "01000-000"),
                "Brasileira",
                "09h às 18h",
                Long.MAX_VALUE
        );

        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Usuário (dono) não encontrado com id: " + Long.MAX_VALUE));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidRestaurantePayload() throws Exception {
        RestauranteRequestDTO dto = new RestauranteRequestDTO(
                "",
                new EnderecoDTO("", "", "", "", ""),
                "",
                "",
                null
        );

        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
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

    private JsonNode readJsonArray(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
