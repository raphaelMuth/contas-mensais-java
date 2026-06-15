package com.raphael.contasmensais.api.financeiro.category;

import com.raphael.contasmensais.entity.financeiro.entity.Category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String icon,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIcon(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
