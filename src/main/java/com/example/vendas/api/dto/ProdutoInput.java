package com.example.vendas.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoInput(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 60) String sku,
        @NotNull @DecimalMin(value = "0.00") BigDecimal preco,
        @NotNull @Min(0) Integer estoque,
        Boolean ativo
) {}

