package br.com.fiap.usuarios.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI usuarios() {
        return new OpenAPI()
                .info(
                    new Info().title("Sistema de Gestão de Restaurantes API")
                        .description("""
                                API para gerenciamento de usuários, tipos de usuário, restaurantes e itens de cardápio.
                                
                                **Módulo de Usuários:**
                                - Criação, atualização e exclusão de usuários
                                - Busca por nome (GET /v1/usuarios?nome=...)
                                - Troca de senha (PATCH /v1/usuarios/{id}/senha)
                                - Atualização de informações (PUT /v1/usuarios/{id})
                                - Validação de login (POST /v1/usuarios/login)
                                - Garantia de e-mail único

                                **Módulo de Tipos de Usuário:**
                                - CRUD de tipos de usuário (ex: DONO_RESTAURANTE, CLIENTE)
                                - Associação com usuários via tipoUsuarioId

                                **Módulo de Restaurantes:**
                                - CRUD completo de restaurantes, associados a um usuário dono

                                **Módulo de Itens do Cardápio:**
                                - CRUD dos itens vendidos por cada restaurante
                                """)
                        .version("1.0.0")
                        .license(new License().name("MIT License"))
                )
                .tags(List.of(
                        new Tag().name("Usuários").description("Gerenciamento de usuários (Donos de Restaurante e Clientes)"),
                        new Tag().name("Tipos de Usuário").description("CRUD de tipos de usuário"),
                        new Tag().name("Restaurantes").description("CRUD de restaurantes"),
                        new Tag().name("Itens do Cardápio").description("CRUD dos itens vendidos no cardápio de um restaurante")
                ));
    }
}
