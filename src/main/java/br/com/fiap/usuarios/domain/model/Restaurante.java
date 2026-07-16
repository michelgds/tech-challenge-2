package br.com.fiap.usuarios.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Restaurante {
    private Long id;
    private String nome;
    private Endereco endereco;
    private String tipoCozinha;
    private String horarioFuncionamento;
    private Long donoId;
}
