package com.financeapi.scheduler;

import com.financeapi.service.BudgetAlertService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertScheduler {

    private final BudgetAlertService budgetAlertService;



    @Scheduled(cron = "0 0 8 * * *")
    public void checkBudgetAlerts() {
        budgetAlertService.runAlerts();
    }
}