package com.raphael.contasmensais.domain.financeiro.repository.transaction;

import com.raphael.contasmensais.domain.financeiro.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> findById(Long id);

    List<Transaction> findByCategoryId(Long categoryId);

    List<Transaction> findByUserId(Long userId);

    Transaction save(Transaction transaction);

    void deleteById(Long id);

    boolean existsById(Long id);
}
