package com.financeapi.service;

import com.financeapi.dto.request.BudgetRequest;
import com.financeapi.dto.response.BudgetResponse;
import com.financeapi.exception.DuplicateResourceException;
import com.financeapi.exception.ResourceNotFoundException;
import com.financeapi.model.Budget;
import com.financeapi.model.User;
import com.financeapi.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    @Transactional
    public BudgetResponse create(BudgetRequest request) {
        User user = transactionService.getCurrentUser();

        boolean exists = budgetRepository
                .findByUserIdAndCategoryAndMonthAndYear(
                        user.getId(), request.getCategory(), request.getMonth(), request.getYear())
                .isPresent();

        if (exists) {
            throw new DuplicateResourceException(
                    "Budget already exists for " + request.getCategory()
                    + " in " + request.getMonth() + "/" + request.getYear()
                    + ". Use update instead.");
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(request.getCategory())
                .monthlyLimit(request.getMonthlyLimit())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        Budget saved = budgetRepository.save(budget);
        log.info("Budget created: id={} user={}", saved.getId(), user.getEmail());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAll(Integer month, Integer year) {
        User user = transactionService.getCurrentUser();
        return budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetResponse getById(Long id) {
        User user = transactionService.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        return toResponse(budget);
    }

    @Transactional
    public BudgetResponse update(Long id, BudgetRequest request) {
        User user = transactionService.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        budget.setCategory(request.getCategory());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget updated = budgetRepository.save(budget);
        log.info("Budget updated: id={}", id);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        User user = transactionService.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        budgetRepository.delete(budget);
        log.info("Budget deleted: id={}", id);
    }

    private BudgetResponse toResponse(Budget b) {
        return BudgetResponse.builder()
                .id(b.getId())
                .category(b.getCategory())
                .monthlyLimit(b.getMonthlyLimit())
                .month(b.getMonth())
                .year(b.getYear())
                .build();
    }
}
