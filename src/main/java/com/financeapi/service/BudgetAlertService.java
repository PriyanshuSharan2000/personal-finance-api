package com.financeapi.service;

import com.financeapi.repository.BudgetRepository;
import com.financeapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public void runAlerts() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        log.info("Running budget alert check for {}/{}", month, year);

        var budgets = budgetRepository.findByMonthAndYear(month, year);

        for (var budget : budgets) {
            String userEmail = budget.getUser().getEmail();
            Long userId = budget.getUser().getId();

            BigDecimal spent = transactionRepository.sumExpenseByCategoryAndMonth(
                    userId, budget.getCategory(), month, year);

            if (budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) == 0) continue;

            double ratio = spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                    .doubleValue();

            if (ratio > 1.0) {
                log.warn("[BUDGET ALERT] User {} EXCEEDED budget for {}. Spent: {} / Limit: {}",
                        userEmail, budget.getCategory(), spent, budget.getMonthlyLimit());
            } else if (ratio >= 0.8) {
                log.warn("[BUDGET WARNING] User {} used {}% of {} budget. Spent: {} / Limit: {}",
                        userEmail, Math.round(ratio * 100),
                        budget.getCategory(), spent, budget.getMonthlyLimit());
            }
        }

        log.info("Budget alert check completed. Checked {} budgets.", budgets.size());
    }
}