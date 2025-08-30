package com.example.vendas.api.dto;

import java.time.OffsetDateTime;

public record ClienteOutput(
        Long id,
        String nome,
        String email,
        String documento,
        OffsetDateTime criadoEm
) {}

