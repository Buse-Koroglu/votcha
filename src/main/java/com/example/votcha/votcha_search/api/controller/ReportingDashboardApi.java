package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.api.dto.response.UserVoteSuccessSyncResponse;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/dashboard/stats")
@Tag(name = "Dashboard Reporting", description = "APIs for retrieving dashboard statistics and analytics")
public interface ReportingDashboardApi {
    @GetMapping("/registrations/today")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    Long getTodayRegistrations();

    @GetMapping("/registrations/total")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    Long getAllRegistrations();

    @GetMapping("/events/today")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    Long getTodayEvents();

    @GetMapping("/events/total")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    Long getAllEvents();

    @GetMapping("/events/trends")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    List<EventDocument> getMostVotedEvents(@RequestParam(defaultValue = "5")int limit);

    @GetMapping("/votes/today")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    Long getTodayVotes();

    @GetMapping("/votes/total")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    Long getAllVotes();

    @GetMapping("/voters/{voterId}/success-info")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    UserVoteSuccessSyncResponse getVoterSuccessCount(@PathVariable String voterId);
}
