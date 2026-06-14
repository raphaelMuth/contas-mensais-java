package com.raphael.contasmensais.api.financeiro.transaction;

import com.raphael.contasmensais.api.financeiro.category.CategoryResponse;
import com.raphael.contasmensais.domain.financeiro.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDate transactionDate,
        String description,
        String externalId,
        Long userId,
        CategoryResponse category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getExternalId(),
                transaction.getUserId(),
                CategoryResponse.from(transaction.getCategory()),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
