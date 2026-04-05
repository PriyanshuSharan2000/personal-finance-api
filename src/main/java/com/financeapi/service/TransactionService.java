package com.financeapi.service;

import com.financeapi.dto.request.TransactionRequest;
import com.financeapi.dto.response.TransactionResponse;
import com.financeapi.exception.ResourceNotFoundException;
import com.financeapi.model.Transaction;
import com.financeapi.model.User;
import com.financeapi.repository.TransactionRepository;
import com.financeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        User user = getCurrentUser();

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(request.getType())
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .txnDate(request.getTxnDate())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: id={} user={}", saved.getId(), user.getEmail());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(
            Transaction.TransactionType type,
            Transaction.Category category,
            LocalDate from,
            LocalDate to) {

        User user = getCurrentUser();

        LocalDate effectiveFrom = (from != null) ? from : LocalDate.now().withDayOfYear(1);
        LocalDate effectiveTo = (to != null) ? to : LocalDate.now();

        return transactionRepository
                .findWithFilters(user.getId(), type, category, effectiveFrom, effectiveTo)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(Long id) {
        Transaction transaction = findByIdAndUser(id);
        return toResponse(transaction);
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction transaction = findByIdAndUser(id);

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setTxnDate(request.getTxnDate());

        Transaction updated = transactionRepository.save(transaction);
        log.info("Transaction updated: id={}", id);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Transaction transaction = findByIdAndUser(id);
        transactionRepository.delete(transaction);
        log.info("Transaction deleted: id={}", id);
    }

    private Transaction findByIdAndUser(Long id) {
        User user = getCurrentUser();
        return transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .type(t.getType())
                .amount(t.getAmount())
                .category(t.getCategory())
                .description(t.getDescription())
                .txnDate(t.getTxnDate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
