package com.example.votcha.service;

import com.example.votcha.events.domain.exception.EventDeadlinePassedException;
import com.example.votcha.events.domain.model.Event;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.votes.api.dto.VoteRequestDto;
import com.example.votcha.votes.domain.exception.AlreadyVotedException;
import com.example.votcha.votes.domain.repository.VoteRepository;
import com.example.votcha.votes.service.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {
    @Mock private VoteRepository voteRepository;
    @Mock private OptionRepository optionRepository;
    @InjectMocks private VoteService voteService;

    @Test
    @DisplayName("GIVEN user already voted WHEN try to vote another option THEN throw AlreadyVotedException")
    void givenUserAlreadyVoted_whenTryTooVoteAnotherOption_thenThrowAlreadyVotedException(){
        // Arrange
        String userId = "u-123";
        String optionId = "op-2";
        String eventId = "e-123";

        Event event = Event.builder().id(eventId).build();
        Option secondOption = Option.builder().id(optionId).event(event).build();


        VoteRequestDto request = new VoteRequestDto("op-2");
        given(optionRepository.findById(optionId)).willReturn(Optional.of(secondOption));
        given(voteRepository.existsByVoterIdAndOption_Event_Id(userId,eventId)).willReturn(true);

         // Act & Assert
        assertThatThrownBy(() -> voteService.createVote(request,userId))
                .isInstanceOf(AlreadyVotedException.class);

    }

    @Test
    @DisplayName("GIVEN deadline is expired WHEN try to vote an Event THEN throw EventDeadlinePassedException")
    void givenDeadlineExpired_whenTryToVoteAnEvent_thenThrowEventDeadlinePassedException(){
        String userId = "u-123";
        String optionId = "op-2";
        String eventId = "e-123";

        Event event = Event.builder().id(eventId).deadline(Instant.now().minusSeconds(100)).build();
        Option secondOption = Option.builder().id(optionId).event(event).build();


        VoteRequestDto request = new VoteRequestDto("op-2");
        given(optionRepository.findById(optionId)).willReturn(Optional.of(secondOption));


        // Act & Assert
        assertThatThrownBy(() -> voteService.createVote(request,userId))
                .isInstanceOf(EventDeadlinePassedException.class);
    }

    // todo - Başarılı oy
    // todo - OptionNotFound
    // todo - Past deadline
    // todo - Unauthorized update/delete
}
