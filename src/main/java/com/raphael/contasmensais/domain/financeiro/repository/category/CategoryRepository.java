package com.raphael.contasmensais.domain.financeiro.repository.category;

import com.raphael.contasmensais.domain.financeiro.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(Long id);

    List<Category> findAll();

    Category save(Category category);

    void deleteById(Long id);

    boolean existsById(Long id);
}
