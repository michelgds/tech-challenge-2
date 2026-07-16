package br.com.fiap.usuarios.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemCardapio {
    private Long id;
    private Long restauranteId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean disponivelSomenteLocal;
    private String fotoPath;
}
