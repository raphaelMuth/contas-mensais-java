package com.raphael.contasmensais.service.financeiro.category;

import com.raphael.contasmensais.domain.financeiro.entity.Category;
import com.raphael.contasmensais.domain.financeiro.repository.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    private Category buildCategory(Long id) {
        return Category.builder()
                .id(id)
                .name("Alimentação")
                .description("Gastos com comida")
                .icon("🍔")
                .build();
    }

    @Test
    void findById_whenExists_returnsCategory() {
        Category category = buildCategory(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(1L);

        assertThat(result).isEqualTo(category);
    }

    @Test
    void findById_whenNotFound_throwsNoSuchElementException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_returnsAllCategories() {
        List<Category> categories = List.of(buildCategory(1L), buildCategory(2L));
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void create_savesAndReturnsCategory() {
        Category input = buildCategory(null);
        Category saved = buildCategory(1L);
        when(categoryRepository.save(input)).thenReturn(saved);

        Category result = categoryService.create(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(categoryRepository).save(input);
    }

    @Test
    void update_whenExists_updatesFieldsAndReturns() {
        Category existing = buildCategory(1L);
        Category updated = Category.builder()
                .name("Transporte")
                .description("Gastos com transporte")
                .icon("🚗")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        Category result = categoryService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Transporte");
        assertThat(result.getDescription()).isEqualTo("Gastos com transporte");
        assertThat(result.getIcon()).isEqualTo("🚗");
    }

    @Test
    void update_whenNotFound_throwsNoSuchElementException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(99L, buildCategory(null)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_whenExists_deletesSuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_throwsNoSuchElementException() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");

        verify(categoryRepository, never()).deleteById(any());
    }
}
