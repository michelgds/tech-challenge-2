package br.com.fiap.techchallenge.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Endereco {
    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;
}

