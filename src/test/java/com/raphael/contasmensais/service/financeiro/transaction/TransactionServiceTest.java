package com.raphael.contasmensais.service.financeiro.transaction;

import com.raphael.contasmensais.entity.financeiro.entity.Category;
import com.raphael.contasmensais.entity.financeiro.entity.Transaction;
import com.raphael.contasmensais.entity.financeiro.repository.transaction.TransactionRepository;
import com.raphael.contasmensais.service.financeiro.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    CategoryService categoryService;

    @InjectMocks
    TransactionService transactionService;

    private Category buildCategory(Long id) {
        return Category.builder()
                .id(id)
                .name("Alimentação")
                .description("Gastos com comida")
                .icon("🍔")
                .build();
    }

    private Transaction buildTransaction(Long id, Category category) {
        return Transaction.builder()
                .id(id)
                .amount(new BigDecimal("49.90"))
                .transactionDate(LocalDate.of(2026, 6, 14))
                .userId(1L)
                .category(category)
                .build();
    }

    @Test
    void findById_whenExists_returnsTransaction() {
        Transaction transaction = buildTransaction(1L, buildCategory(1L));
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.findById(1L);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    void findById_whenNotFound_throwsNoSuchElementException() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByUserId_returnsList() {
        List<Transaction> list = List.of(buildTransaction(1L, buildCategory(1L)));
        when(transactionRepository.findByUserId(1L)).thenReturn(list);

        List<Transaction> result = transactionService.findByUserId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_withNewCategory_createsCategory() {
        // categoria sem id = nova categoria
        Category newCategory = buildCategory(null);
        Category savedCategory = buildCategory(10L);
        Transaction transaction = buildTransaction(null, newCategory);
        Transaction saved = buildTransaction(1L, savedCategory);

        when(categoryService.create(newCategory)).thenReturn(savedCategory);
        when(transactionRepository.save(transaction)).thenReturn(saved);

        Transaction result = transactionService.create(transaction);

        verify(categoryService).create(newCategory);
        verify(categoryService, never()).findById(any());
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void create_withExistingCategory_loadsCategory() {
        // categoria com id = busca a existente
        Category categoryRef = buildCategory(5L);
        Category categoryFromDb = buildCategory(5L);
        Transaction transaction = buildTransaction(null, categoryRef);
        Transaction saved = buildTransaction(1L, categoryFromDb);

        when(categoryService.findById(5L)).thenReturn(categoryFromDb);
        when(transactionRepository.save(transaction)).thenReturn(saved);

        Transaction result = transactionService.create(transaction);

        verify(categoryService).findById(5L);
        verify(categoryService, never()).create(any());
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void update_withExistingCategory_updatesFields() {
        Category existingCategory = buildCategory(5L);
        Transaction existing = buildTransaction(1L, existingCategory);
        Transaction updated = Transaction.builder()
                .amount(new BigDecimal("99.00"))
                .transactionDate(LocalDate.of(2026, 6, 15))
                .description("Jantar")
                .category(buildCategory(5L))
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryService.findById(5L)).thenReturn(existingCategory);
        when(transactionRepository.save(existing)).thenReturn(existing);

        Transaction result = transactionService.update(1L, updated);

        assertThat(result.getAmount()).isEqualByComparingTo("99.00");
        assertThat(result.getDescription()).isEqualTo("Jantar");
    }

    @Test
    void delete_whenExists_deletesSuccessfully() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.delete(1L);

        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_throwsNoSuchElementException() {
        when(transactionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");

        verify(transactionRepository, never()).deleteById(any());
    }
}
