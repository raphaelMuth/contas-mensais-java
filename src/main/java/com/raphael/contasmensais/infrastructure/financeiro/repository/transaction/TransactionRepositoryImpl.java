package com.raphael.contasmensais.infrastructure.financeiro.repository.transaction;

import com.raphael.contasmensais.domain.financeiro.entity.Transaction;
import com.raphael.contasmensais.domain.financeiro.repository.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpa;

    @Override
    public Optional<Transaction> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Transaction> findByCategoryId(Long categoryId) {
        return jpa.findByCategoryId(categoryId);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return jpa.findByUserId(userId);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return jpa.save(transaction);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }
}
