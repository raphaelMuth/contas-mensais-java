package com.raphael.contasmensais.infrastructure.financeiro.repository.category;

import com.raphael.contasmensais.entity.financeiro.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}
