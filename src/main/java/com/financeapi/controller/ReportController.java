package com.financeapi.controller;

import com.financeapi.dto.response.ApiResponse;
import com.financeapi.dto.response.MonthlyReportResponse;
import com.financeapi.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reports", description = "Monthly financial reports and budget status")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly income vs expense summary with category breakdown")
    public ResponseEntity<ApiResponse<MonthlyReportResponse>> getMonthlyReport(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {

        int effectiveMonth = month == 0 ? LocalDate.now().getMonthValue() : month;
        int effectiveYear = year == 0 ? LocalDate.now().getYear() : year;

        MonthlyReportResponse report = reportService.getMonthlyReport(effectiveMonth, effectiveYear);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/budget-status")
    @Operation(summary = "Get current month budget usage per category")
    public ResponseEntity<ApiResponse<List<MonthlyReportResponse.CategoryBreakdown>>> getBudgetStatus() {
        List<MonthlyReportResponse.CategoryBreakdown> status = reportService.getBudgetStatus();
        return ResponseEntity.ok(ApiResponse.success(status));
    }
}
