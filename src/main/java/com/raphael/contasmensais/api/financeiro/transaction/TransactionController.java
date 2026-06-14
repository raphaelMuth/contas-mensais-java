package com.raphael.contasmensais.api.financeiro.transaction;

import com.raphael.contasmensais.domain.financeiro.entity.Category;
import com.raphael.contasmensais.domain.financeiro.entity.Transaction;
import com.raphael.contasmensais.service.financeiro.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable Long id) {
        return TransactionResponse.from(transactionService.findById(id));
    }

    @GetMapping
    public List<TransactionResponse> findAll(@RequestParam Long userId) {
        return transactionService.findByUserId(userId).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = toEntity(request);
        return TransactionResponse.from(transactionService.create(transaction));
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = toEntity(request);
        return TransactionResponse.from(transactionService.update(id, transaction));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transactionService.delete(id);
    }

    private Transaction toEntity(TransactionRequest request) {
        TransactionRequest.CategoryInput categoryInput = request.category();
        Category category = Category.builder()
                .id(categoryInput.id())
                .name(categoryInput.name())
                .description(categoryInput.description())
                .icon(categoryInput.icon())
                .build();

        return Transaction.builder()
                .amount(request.amount())
                .transactionDate(request.transactionDate())
                .description(request.description())
                .externalId(request.externalId())
                .userId(request.userId())
                .category(category)
                .build();
    }
}
