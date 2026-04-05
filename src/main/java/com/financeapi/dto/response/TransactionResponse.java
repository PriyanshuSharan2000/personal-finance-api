package com.financeapi.dto.response;

import com.financeapi.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private Transaction.Category category;
    private String description;
    private LocalDate txnDate;
    private LocalDateTime createdAt;
}
