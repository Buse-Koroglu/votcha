package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.service.DashboardReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Override
    public ResponseEntity<Long> getTodayEvents() {
        return ResponseEntity.ok(dashboardReportingService.getTodayEventsCount());
    }

    @Override
    public ResponseEntity<Long> getAllEvents() {
        return ResponseEntity.ok(dashboardReportingService.getAllEventsCount());
    }

    @Override
    public ResponseEntity<List<EventDocument>> getMostVotedEvents(int limit) {
        return ResponseEntity.ok(dashboardReportingService.getMostVotedEvents(limit));
    }
}
