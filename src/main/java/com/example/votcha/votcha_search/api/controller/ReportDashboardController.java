package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.api.dto.response.UserVoteSuccessSyncResponse;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.service.DashboardReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportDashboardController implements ReportingDashboardApi{
    private final DashboardReportingService  dashboardReportingService;


    @Override
    public Long getTodayRegistrations() {
        return dashboardReportingService.getTodayRegistrationCount();
    }

    @Override
    public Long getAllRegistrations() {
        return dashboardReportingService.getAllUsersCount();
    }

    @Override
    public Long getTodayEvents() {
        return dashboardReportingService.getTodayEventsCount();
    }

    @Override
    public Long getAllEvents() {
        return dashboardReportingService.getAllEventsCount();
    }

    @Override
    public List<EventDocument> getMostVotedEvents(int limit) {
        return dashboardReportingService.getMostVotedEvents(limit);
    }

    @Override
    public Long getTodayVotes() {return dashboardReportingService.getTodayVotesCount();}

    @Override
    public Long getAllVotes() {return dashboardReportingService.getAllVotesCount();}

    @Override
    public UserVoteSuccessSyncResponse getVoterSuccessCount(String voterId) { return dashboardReportingService.getVoterSuccessCount(voterId);}
}
