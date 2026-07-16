package br.com.fiap.usuarios.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TipoUsuario {
    private Long id;
    private String nome;
}
