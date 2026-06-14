package com.raphael.contasmensais.infrastructure.financeiro.repository.category;

import com.raphael.contasmensais.domain.financeiro.entity.Category;
import com.raphael.contasmensais.domain.financeiro.repository.category.CategoryRepository;
import com.raphael.contasmensais.infrastructure.financeiro.repository.category.CategoryRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CategoryRepositoryImpl.class)
class CategoryRepositoryImplTest {

    @Autowired
    CategoryRepository categoryRepository;

    private Category buildCategory(String name) {
        return Category.builder()
                .name(name)
                .description("Descrição de " + name)
                .icon("📦")
                .build();
    }

    @Test
    void save_persistsAndReturnsWithId() {
        Category saved = categoryRepository.save(buildCategory("Alimentação"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_whenExists_returnsCategory() {
        Category saved = categoryRepository.save(buildCategory("Transporte"));

        Optional<Category> result = categoryRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Transporte");
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        Optional<Category> result = categoryRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllSaved() {
        categoryRepository.save(buildCategory("Saúde"));
        categoryRepository.save(buildCategory("Lazer"));

        List<Category> result = categoryRepository.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void existsById_whenExists_returnsTrue() {
        Category saved = categoryRepository.save(buildCategory("Educação"));

        assertThat(categoryRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    void existsById_whenNotExists_returnsFalse() {
        assertThat(categoryRepository.existsById(999L)).isFalse();
    }

    @Test
    void deleteById_removesEntity() {
        Category saved = categoryRepository.save(buildCategory("Vestuário"));

        categoryRepository.deleteById(saved.getId());

        assertThat(categoryRepository.findById(saved.getId())).isEmpty();
    }
}
