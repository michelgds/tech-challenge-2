package br.com.fiap.usuarios.infrastructure.web.controller;

import br.com.fiap.usuarios.application.dto.common.EnderecoDTO;
import br.com.fiap.usuarios.application.dto.usuario.LoginRequestDTO;
import br.com.fiap.usuarios.application.dto.usuario.SenhaUpdateDTO;
import br.com.fiap.usuarios.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.usuarios.application.dto.usuario.UsuarioUpdateDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteUsuarioCrudAndLoginFlow() throws Exception {
        String suffix = uniqueSuffix();
        Long tipoUsuarioId = findTipoUsuarioIdByName("CLIENTE");
        UsuarioRequestDTO createDto = new UsuarioRequestDTO(
                "Usuário " + suffix,
                "usuario-" + suffix + "@email.com",
                "login-" + suffix,
                "senha123",
                tipoUsuarioId,
                new EnderecoDTO("Rua Um", "10", "São Paulo", "SP", "01000-000")
        );

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        JsonNode created = findUsuarioByEmail(createDto.nome(), createDto.email());
        long usuarioId = created.path("id").asLong();

        mockMvc.perform(get("/v1/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId))
                .andExpect(jsonPath("$.email").value(createDto.email()));

        UsuarioUpdateDTO updateDto = new UsuarioUpdateDTO(
                "Usuário Atualizado " + suffix,
                "usuario-atualizado-" + suffix + "@email.com",
                "login-atualizado-" + suffix,
                tipoUsuarioId,
                new EnderecoDTO("Rua Dois", "20", "Campinas", "SP", "13000-000")
        );

        mockMvc.perform(put("/v1/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        JsonNode updated = findUsuarioByEmail(updateDto.nome(), updateDto.email());
        assertThat(updated.path("login").asText()).isEqualTo(updateDto.login());
        assertThat(updated.path("endereco").path("cidade").asText()).isEqualTo("Campinas");

        mockMvc.perform(patch("/v1/usuarios/{id}/senha", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SenhaUpdateDTO("senha123", "novaSenha123"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO(updateDto.login(), "novaSenha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId));

        mockMvc.perform(delete("/v1/usuarios/{id}", usuarioId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    void shouldReturnBusinessErrorForDuplicateEmail() throws Exception {
        Long tipoUsuarioId = findTipoUsuarioIdByName("CLIENTE");
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Duplicado",
                "admin@restaurante.com",
                "duplicado-" + uniqueSuffix(),
                "senha123",
                tipoUsuarioId,
                new EnderecoDTO("Rua", "1", "São Paulo", "SP", "01000-000")
        );

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("E-mail já cadastrado: admin@restaurante.com"));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidUsuarioPayload() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "",
                "email-invalido",
                "",
                "",
                null,
                new EnderecoDTO("", "", "", "", "")
        );

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        mockMvc.perform(post("/v1/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("admin", "senha-errada"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Login ou senha inválidos"));
    }

    @Test
    void shouldReturnBadRequestForInvalidUsuarioIdType() throws Exception {
        mockMvc.perform(get("/v1/usuarios/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Tipo de parâmetro inválido"));
    }

    private Long findTipoUsuarioIdByName(String nome) throws Exception {
        JsonNode tipos = readJsonArray(mockMvc.perform(get("/v1/tipos-usuario"))
                .andExpect(status().isOk())
                .andReturn());
        for (JsonNode tipo : tipos) {
            if (nome.equals(tipo.path("nome").asText())) {
                return tipo.path("id").asLong();
            }
        }
        throw new AssertionError("Tipo de usuário não encontrado: " + nome);
    }

    private JsonNode findUsuarioByEmail(String nome, String email) throws Exception {
        JsonNode usuarios = readJsonArray(mockMvc.perform(get("/v1/usuarios").param("nome", nome))
                .andExpect(status().isOk())
                .andReturn());
        for (JsonNode usuario : usuarios) {
            if (email.equals(usuario.path("email").asText())) {
                return usuario;
            }
        }
        throw new AssertionError("Usuário não encontrado: " + email);
    }

    private JsonNode readJsonArray(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
