package br.com.fiap.techchallenge.domain.model;

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
