package br.com.fiap.usuarios.infrastructure.web.controller;

import br.com.fiap.usuarios.application.dto.tipousuario.TipoUsuarioRequestDTO;
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
class TipoUsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteTipoUsuarioCrudFlow() throws Exception {
        String nome = "TIPO_" + uniqueSuffix();
        TipoUsuarioRequestDTO createDto = new TipoUsuarioRequestDTO(nome);

        mockMvc.perform(post("/v1/tipos-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        JsonNode created = findTipoByName(nome);
        long tipoId = created.path("id").asLong();

        mockMvc.perform(get("/v1/tipos-usuario/{id}", tipoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(nome));

        String updatedName = nome + "_ATUALIZADO";
        mockMvc.perform(put("/v1/tipos-usuario/{id}", tipoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TipoUsuarioRequestDTO(updatedName))))
                .andExpect(status().isNoContent());

        JsonNode updated = findTipoByName(updatedName);
        assertThat(updated.path("id").asLong()).isEqualTo(tipoId);

        mockMvc.perform(delete("/v1/tipos-usuario/{id}", tipoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/tipos-usuario/{id}", tipoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    void shouldReturnBusinessErrorForDuplicateTipoUsuario() throws Exception {
        mockMvc.perform(post("/v1/tipos-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TipoUsuarioRequestDTO("CLIENTE"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("Tipo de usuário já cadastrado: CLIENTE"));
    }

    @Test
    void shouldReturnValidationErrorForInvalidTipoUsuarioPayload() throws Exception {
        mockMvc.perform(post("/v1/tipos-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TipoUsuarioRequestDTO(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    @Test
    void shouldBlockDeletionWhenTipoUsuarioHasAssociatedUsuarios() throws Exception {
        long tipoId = findTipoByName("CLIENTE").path("id").asLong();

        mockMvc.perform(delete("/v1/tipos-usuario/{id}", tipoId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("Não é possível excluir: existem usuários associados a este tipo"));
    }

    private JsonNode findTipoByName(String nome) throws Exception {
        JsonNode tipos = readJsonArray(mockMvc.perform(get("/v1/tipos-usuario"))
                .andExpect(status().isOk())
                .andReturn());
        for (JsonNode tipo : tipos) {
            if (nome.equals(tipo.path("nome").asText())) {
                return tipo;
            }
        }
        throw new AssertionError("Tipo de usuário não encontrado: " + nome);
    }

    private JsonNode readJsonArray(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
