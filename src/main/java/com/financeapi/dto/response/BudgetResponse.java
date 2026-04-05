package com.financeapi.dto.response;

import com.financeapi.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetResponse {
    private Long id;
    private Transaction.Category category;
    private BigDecimal monthlyLimit;
    private Integer month;
    private Integer year;
}
