package com.financeapi.service;

import com.financeapi.dto.response.MonthlyReportResponse;
import com.financeapi.model.Budget;
import com.financeapi.model.Transaction;
import com.financeapi.model.User;
import com.financeapi.repository.BudgetRepository;
import com.financeapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(int month, int year) {
        User user = transactionService.getCurrentUser();

        BigDecimal totalIncome = transactionRepository.sumByTypeAndMonth(
                user.getId(), Transaction.TransactionType.INCOME, month, year);
        BigDecimal totalExpense = transactionRepository.sumByTypeAndMonth(
                user.getId(), Transaction.TransactionType.EXPENSE, month, year);

        BigDecimal netSavings = totalIncome.subtract(totalExpense).setScale(2, RoundingMode.HALF_UP);

        // Category breakdown
        List<Object[]> categoryData = transactionRepository
                .sumExpenseGroupedByCategory(user.getId(), month, year);

        Map<Transaction.Category, BigDecimal> spendMap = new HashMap<>();
        for (Object[] row : categoryData) {
            spendMap.put((Transaction.Category) row[0], (BigDecimal) row[1]);
        }

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year);
        Map<Transaction.Category, BigDecimal> budgetMap = new HashMap<>();
        for (Budget b : budgets) {
            budgetMap.put(b.getCategory(), b.getMonthlyLimit());
        }

        List<MonthlyReportResponse.CategoryBreakdown> breakdown = new ArrayList<>();

        // Include all categories that have either spending or a budget
        java.util.Set<Transaction.Category> categories = new java.util.HashSet<>();
        categories.addAll(spendMap.keySet());
        categories.addAll(budgetMap.keySet());

        for (Transaction.Category cat : categories) {
            BigDecimal spent = spendMap.getOrDefault(cat, BigDecimal.ZERO);
            BigDecimal budget = budgetMap.getOrDefault(cat, null);
            String status = computeStatus(spent, budget);

            breakdown.add(MonthlyReportResponse.CategoryBreakdown.builder()
                    .category(cat.name())
                    .spent(spent.setScale(2, RoundingMode.HALF_UP))
                    .budget(budget != null ? budget.setScale(2, RoundingMode.HALF_UP) : null)
                    .status(status)
                    .build());
        }

        breakdown.sort((a, b) -> b.getSpent().compareTo(a.getSpent()));

        return MonthlyReportResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome.setScale(2, RoundingMode.HALF_UP))
                .totalExpense(totalExpense.setScale(2, RoundingMode.HALF_UP))
                .netSavings(netSavings)
                .categoryBreakdown(breakdown)
                .build();
    }

    private String computeStatus(BigDecimal spent, BigDecimal budget) {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) == 0) return "NO_BUDGET";
        double ratio = spent.divide(budget, 4, RoundingMode.HALF_UP).doubleValue();
        if (ratio > 1.0) return "OVER_BUDGET";
        if (ratio >= 1.0) return "AT_LIMIT";
        if (ratio >= 0.8) return "WARNING";
        return "ON_TRACK";
    }

    @Transactional(readOnly = true)
    public List<MonthlyReportResponse.CategoryBreakdown> getBudgetStatus() {
        User user = transactionService.getCurrentUser();
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year);
        List<MonthlyReportResponse.CategoryBreakdown> result = new ArrayList<>();

        for (Budget b : budgets) {
            BigDecimal spent = transactionRepository.sumExpenseByCategoryAndMonth(
                    user.getId(), b.getCategory(), month, year);
            String status = computeStatus(spent, b.getMonthlyLimit());

            result.add(MonthlyReportResponse.CategoryBreakdown.builder()
                    .category(b.getCategory().name())
                    .spent(spent.setScale(2, RoundingMode.HALF_UP))
                    .budget(b.getMonthlyLimit().setScale(2, RoundingMode.HALF_UP))
                    .status(status)
                    .build());
        }

        return result;
    }
}
