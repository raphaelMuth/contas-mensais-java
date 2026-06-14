package com.raphael.contasmensais.api.financeiro.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate transactionDate,
        String description,
        String externalId,
        Long userId,
        @NotNull CategoryInput category
) {
    // id preenchido = categoria existente; id nulo = nova categoria
    public record CategoryInput(
            Long id,
            String name,
            String description,
            String icon
    ) {}
}
