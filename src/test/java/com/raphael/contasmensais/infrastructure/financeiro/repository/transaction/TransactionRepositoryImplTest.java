package com.raphael.contasmensais.infrastructure.financeiro.repository.transaction;

import com.raphael.contasmensais.config.JpaAuditingTestConfig;
import com.raphael.contasmensais.domain.financeiro.entity.Category;
import com.raphael.contasmensais.domain.financeiro.entity.Transaction;
import com.raphael.contasmensais.domain.financeiro.repository.category.CategoryRepository;
import com.raphael.contasmensais.domain.financeiro.repository.transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingTestConfig.class)
class TransactionRepositoryImplTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(Category.builder()
                .name("Alimentação")
                .description("Gastos com comida")
                .icon("🍔")
                .build());
    }

    private Transaction buildTransaction(Long userId, String externalId) {
        return Transaction.builder()
                .amount(new BigDecimal("49.90"))
                .transactionDate(LocalDate.of(2026, 6, 14))
                .userId(userId)
                .externalId(externalId)
                .category(category)
                .build();
    }

    @Test
    void save_persistsAndReturnsWithId() {
        Transaction saved = transactionRepository.save(buildTransaction(1L, null));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    void findById_whenExists_returnsTransaction() {
        Transaction saved = transactionRepository.save(buildTransaction(1L, null));

        Optional<Transaction> result = transactionRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo("49.90");
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        Optional<Transaction> result = transactionRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_returnsMatchingTransactions() {
        transactionRepository.save(buildTransaction(1L, "EXT-001"));
        transactionRepository.save(buildTransaction(1L, "EXT-002"));
        transactionRepository.save(buildTransaction(2L, "EXT-003"));

        List<Transaction> result = transactionRepository.findByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(t -> t.getUserId().equals(1L));
    }

    @Test
    void findByCategoryId_returnsMatchingTransactions() {
        transactionRepository.save(buildTransaction(1L, null));
        transactionRepository.save(buildTransaction(2L, null));

        List<Transaction> result = transactionRepository.findByCategoryId(category.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void existsById_whenExists_returnsTrue() {
        Transaction saved = transactionRepository.save(buildTransaction(1L, null));

        assertThat(transactionRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    void deleteById_removesEntity() {
        Transaction saved = transactionRepository.save(buildTransaction(1L, null));

        transactionRepository.deleteById(saved.getId());

        assertThat(transactionRepository.findById(saved.getId())).isEmpty();
    }
}
