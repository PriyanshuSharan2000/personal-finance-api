package com.financeapi.repository;

import com.financeapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND t.txnDate BETWEEN :from AND :to " +
           "ORDER BY t.txnDate DESC")
    List<Transaction> findWithFilters(
            @Param("userId") Long userId,
            @Param("type") Transaction.TransactionType type,
            @Param("category") Transaction.Category category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND MONTH(t.txnDate) = :month AND YEAR(t.txnDate) = :year")
    BigDecimal sumByTypeAndMonth(
            @Param("userId") Long userId,
            @Param("type") Transaction.TransactionType type,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND t.category = :category " +
           "AND MONTH(t.txnDate) = :month AND YEAR(t.txnDate) = :year")
    BigDecimal sumExpenseByCategoryAndMonth(
            @Param("userId") Long userId,
            @Param("category") Transaction.Category category,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT t.category, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND MONTH(t.txnDate) = :month AND YEAR(t.txnDate) = :year " +
           "GROUP BY t.category")
    List<Object[]> sumExpenseGroupedByCategory(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    List<Transaction> findByUserId(Long userId);
}
