package com.raphael.contasmensais.infrastructure.financeiro.repository.transaction;

import com.raphael.contasmensais.domain.financeiro.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCategoryId(Long categoryId);

    List<Transaction> findByUserId(Long userId);
}
