package com.example.vendas.api.dto;

import com.example.vendas.domain.model.PedidoStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PedidoOutput(
        Long id,
        Long clienteId,
        PedidoStatus status,
        BigDecimal total,
        OffsetDateTime criadoEm,
        List<ItemOutput> itens
) {
    public record ItemOutput(
            Long produtoId,
            String produtoNome,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}
}

