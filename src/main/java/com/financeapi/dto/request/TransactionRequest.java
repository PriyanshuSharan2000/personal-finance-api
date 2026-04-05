package com.financeapi.dto.request;

import com.financeapi.model.Transaction;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Transaction type is required")
    private Transaction.TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private Transaction.Category category;

    @Size(max = 255)
    private String description;

    @NotNull(message = "Transaction date is required")
    private LocalDate txnDate;
}
