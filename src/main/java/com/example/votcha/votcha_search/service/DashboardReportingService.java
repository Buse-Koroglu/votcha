package com.example.votcha.votcha_search.service;

import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.votcha_search.api.dto.response.UserVoteSuccessSyncResponse;
import com.example.votcha.votcha_search.api.mapper.VoteSuccessElasticMapper;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.domain.repository.EventElasticRepository;
import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import com.example.votcha.votcha_search.domain.repository.VoteElasticRepository;
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
    private final VoteElasticRepository voteElasticRepository;
    private final VoteSuccessElasticMapper voteSuccessElasticMapper;
    private final OptionRepository optionRepository;


    public long getAllUsersCount() {
        return userElasticRepository.count();
    }

    public long getTodayRegistrationCount(){
        Instant startOfToday = getToday();

        return userElasticRepository.countByCreatedAtAfter(startOfToday);
    }

    public long getAllEventsCount() {
        return eventElasticRepository.count();
    }

    public long getTodayEventsCount(){
        Instant startOfToday = getToday();
        return eventElasticRepository.countByCreatedAtAfter(startOfToday);
    }

    public List<EventDocument> getMostVotedEvents(int limit){
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalVoteCount"));

        return eventElasticRepository.findAll(pageable).getContent();
    }

    public long getAllVotesCount() {
        return voteElasticRepository.count();
    }

    public long getTodayVotesCount() {
        return voteElasticRepository.countByCreatedAtAfter(getToday());
    }

    public UserVoteSuccessSyncResponse getVoterSuccessCount(String voterId) {
        long totalVotes = voteElasticRepository.countByVoterId(voterId);

        long totalSuccessVotes = voteElasticRepository
                .countByVoterIdAndIsWinnerTrue(voterId);

        return voteSuccessElasticMapper
                .buildResponse(voterId, totalVotes, totalSuccessVotes);
    }

    public Instant getToday(){
        return LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }
}
