package com.example.votcha.votcha_search.service;

import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.domain.repository.EventElasticRepository;
import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardReportingService {
    private final UserElasticRepository  userElasticRepository;
    private final EventElasticRepository eventElasticRepository;


    public long getAllUsersCount() {
        return userElasticRepository.count();
    }

    public long getTodayRegistrationCount(){
        Instant startOfToday = LocalDate
                .now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        return userElasticRepository.countByCreatedAtAfter(startOfToday);
    }

    public long getAllEventsCount() {
        return eventElasticRepository.count();
    }

    public long getTodayEventsCount(){
        Instant startOfToday = LocalDate
                .now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
        return eventElasticRepository.countByCreatedAtAfter(startOfToday);
    }

    public List<EventDocument> getMostVotedEvents(int limit){
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalVoteCount"));

        return eventElasticRepository.findAll(pageable).getContent();   }
}
