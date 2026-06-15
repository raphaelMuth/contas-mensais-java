package com.raphael.contasmensais.entity.financeiro.repository.category;

import com.raphael.contasmensais.entity.financeiro.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(Long id);

    List<Category> findAll();

    Category save(Category category);

    void deleteById(Long id);

    boolean existsById(Long id);
}
