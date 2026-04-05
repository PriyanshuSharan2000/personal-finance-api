package com.financeapi.repository;

import com.financeapi.model.Budget;
import com.financeapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(
            Long userId, Transaction.Category category, int month, int year);

    List<Budget> findByMonthAndYear(int month, int year);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);
}
