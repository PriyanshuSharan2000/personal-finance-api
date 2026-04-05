package com.financeapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MonthlyReportResponse {
    private Integer month;
    private Integer year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private List<CategoryBreakdown> categoryBreakdown;

    @Data
    @Builder
    public static class CategoryBreakdown {
        private String category;
        private BigDecimal spent;
        private BigDecimal budget;
        private String status; // ON_TRACK, WARNING, AT_LIMIT, OVER_BUDGET
    }
}
