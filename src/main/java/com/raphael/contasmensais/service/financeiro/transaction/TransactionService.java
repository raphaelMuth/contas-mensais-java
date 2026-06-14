package com.raphael.contasmensais.service.financeiro.transaction;

import com.raphael.contasmensais.domain.financeiro.entity.Category;
import com.raphael.contasmensais.domain.financeiro.entity.Transaction;
import com.raphael.contasmensais.domain.financeiro.repository.transaction.TransactionRepository;
import com.raphael.contasmensais.service.financeiro.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Validated
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByCategoryId(Long categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }

    // Unit of Work: categoria e transacao salvas na mesma transacao de banco.
    // Se a categoria nao tiver ID, e criada. Se tiver, e validada antes de associar.
    @Transactional
    public Transaction create(@Valid Transaction transaction) {
        Category category = transaction.getCategory();

        if (category.getId() == null) {
            category = categoryService.create(category);
        } else {
            category = categoryService.findById(category.getId()); // valida existencia
        }

        transaction.setCategory(category);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Long id, @Valid Transaction updated) {
        Transaction existing = findById(id);

        Category category = updated.getCategory();
        if (category.getId() == null) {
            category = categoryService.create(category);
        } else {
            category = categoryService.findById(category.getId());
        }

        existing.setAmount(updated.getAmount());
        existing.setTransactionDate(updated.getTransactionDate());
        existing.setDescription(updated.getDescription());
        existing.setExternalId(updated.getExternalId());
        existing.setCategory(category);
        return transactionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new NoSuchElementException("Transaction not found: " + id);
        }
        transactionRepository.deleteById(id);
    }
}
