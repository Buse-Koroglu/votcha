package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.domain.model.EventDocument;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

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

    @GetMapping("/events/today")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Long> getTodayEvents();

    @GetMapping("/events/total")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Long> getAllEvents();

    @GetMapping("/events/trends")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<List<EventDocument>> getMostVotedEvents(@RequestParam(defaultValue = "5")int limit);

}
