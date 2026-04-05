package com.financeapi.dto.request;

import com.financeapi.model.Transaction;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {

    @NotNull(message = "Category is required")
    private Transaction.Category category;

    @NotNull(message = "Monthly limit is required")
    @DecimalMin(value = "1.00", message = "Monthly limit must be at least 1")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monthlyLimit;

    @NotNull(message = "Month is required")
    @Min(1) @Max(12)
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(2000)
    private Integer year;
}
