package com.financeapi.controller;

import com.financeapi.dto.request.BudgetRequest;
import com.financeapi.dto.response.ApiResponse;
import com.financeapi.dto.response.BudgetResponse;
import com.financeapi.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Budgets", description = "Manage monthly budgets per category")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Set a monthly budget for a category")
    public ResponseEntity<ApiResponse<BudgetResponse>> create(
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created", response));
    }

    @GetMapping
    @Operation(summary = "Get all budgets for a given month and year")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAll(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().monthValue}") Integer month,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().year}") Integer year) {
        List<BudgetResponse> list = budgetService.getAll(month, year);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a budget by ID")
    public ResponseEntity<ApiResponse<BudgetResponse>> getById(@PathVariable Long id) {
        BudgetResponse response = budgetService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted", null));
    }
}
