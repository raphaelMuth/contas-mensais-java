package com.raphael.contasmensais.api.financeiro.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String icon
) {}
