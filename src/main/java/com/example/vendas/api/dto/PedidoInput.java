package com.example.vendas.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoInput(
        @NotNull Long clienteId,
        @NotEmpty List<@Valid ItemInput> itens
) {
    public record ItemInput(
            @NotNull Long produtoId,
            @NotNull @Min(1) Integer quantidade
    ) {}
}

