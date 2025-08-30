package com.example.vendas.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProdutoOutput(
        Long id,
        String nome,
        String sku,
        BigDecimal preco,
        Integer estoque,
        Boolean ativo,
        OffsetDateTime criadoEm
) {}

