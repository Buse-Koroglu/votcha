package com.example.votcha.votcha_search.service;

import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DashboardReportingService {
    private final UserElasticRepository  userElasticRepository;

    public long getAllUsersCount() {
        return userElasticRepository.count();
    }

    public long getTodayRegistrationCount(){
        Instant startOfToday = LocalDate
                .now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        System.out.println(startOfToday);
        return userElasticRepository.countByCreatedAtAfter(startOfToday);
    }
}
