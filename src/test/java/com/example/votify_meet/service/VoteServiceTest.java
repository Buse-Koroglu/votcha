package com.example.votify_meet.service;

import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.domain.exception.AlreadyVotedException;
import com.example.votify_meet.votes.domain.repository.VoteRepository;
import com.example.votify_meet.votes.service.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        given(voteRepository.existsByVoterIdAndOption_EventId(userId,eventId)).willReturn(true);

         // Act & Assert
        assertThatThrownBy(() -> voteService.createVote(request,userId))
                .isInstanceOf(AlreadyVotedException.class);

    }

    // todo - Başarılı oy
    // todo - OptionNotFound
    // todo - Past deadline
    // todo - Unauthorized update/delete
}
