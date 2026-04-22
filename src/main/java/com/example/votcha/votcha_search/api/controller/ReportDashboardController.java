package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.service.DashboardReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportDashboardController implements ReportingDashboardApi{
    private final DashboardReportingService  dashboardReportingService;


    @Override
    public ResponseEntity<Long> getTodayRegistrations() {
        return ResponseEntity.ok(dashboardReportingService.getTodayRegistrationCount());
    }

    @Override
    public ResponseEntity<Long> getAllRegistrations() {
        return ResponseEntity.ok(dashboardReportingService.getAllUsersCount());
    }
}
