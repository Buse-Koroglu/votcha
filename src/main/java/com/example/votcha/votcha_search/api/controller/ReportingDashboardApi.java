package com.example.votcha.votcha_search.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/dashboard/stats")
@Tag(name = "Dashboard Reporting", description = "APIs for retrieving dashboard statistics and analytics")
public interface ReportingDashboardApi {
    @GetMapping("/registrations/today")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Long> getTodayRegistrations();

    @GetMapping("/registrations/total")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Long> getAllRegistrations();
}
