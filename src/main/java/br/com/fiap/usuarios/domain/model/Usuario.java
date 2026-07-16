package br.com.fiap.usuarios.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private Long tipoUsuarioId;
    /** Nome do tipo de usuário, preenchido apenas em leitura (via JOIN) para conveniência da API. */
    private String tipoUsuarioNome;
    private LocalDateTime dataUltimaAlteracao;
    private Endereco endereco;
}
