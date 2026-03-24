package com.example.votify_meet.service;

import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.repository.EventsRepo;
import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.mapper.OptionMapper;
import com.example.votify_meet.options.domain.exception.UnauthorizedException;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.options.service.OptionService;
import com.example.votify_meet.users.domain.model.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class OptionServiceTest {
    @Mock private OptionRepository optionRepository;
    @Mock private EventsRepo eventsRepo;
    @Mock private OptionMapper optionMapper;
    @InjectMocks private OptionService optionService;

    private final Integer VOTE_COUNT = 1;

    @Test
    @DisplayName("GIVEN event owner WHEN create option THEN return success option response")
    void givenEventOwner_whenCreateOption_thenReturnSuccess(){
        // Arrange
        String eventId = "event-123";
        String ownerId = "user-123";
        Users owner = Users.builder().id(ownerId).build();
        Event event = Event.builder().id(eventId).creator(owner).build();
        OptionRequestDto requestDto = new OptionRequestDto("Option A");

        Option mockOption = Option.builder().id("opt-1").content("Option A").event(event).build();
        OptionResponseDto expectedResponse = new OptionResponseDto("opt-1","Option A", null, null, VOTE_COUNT);

        // Act
        given(eventsRepo.findById(eventId)).willReturn(Optional.of(event));
        given(optionMapper.toEntity(requestDto, event)).willReturn(mockOption);
        given(optionRepository.saveAndFlush(mockOption)).willReturn(mockOption);
        given(optionMapper.toResponse(mockOption)).willReturn(expectedResponse);

        // Act
        OptionResponseDto actualResponse = optionService.createOption(requestDto, eventId, ownerId);

        // Assert
        assertThat(actualResponse).isEqualTo(expectedResponse);

        then(optionRepository).should().saveAndFlush(mockOption);

    }

    @Test
    @DisplayName("GIVEN unauthorized user WHEN create option THEN throw UnauthorizedException")
    void givenUnauthorizedUser_whenCreateOption_thenThrowException(){
        // Arrange
        String eventId = "event-123";
        String ownerId = "user-123";
        String hackerId = "hacker-234";

        Users owner = Users.builder().id(ownerId).build();
        Event event = Event.builder().id(eventId).creator(owner).build();

        OptionRequestDto requestDto = new OptionRequestDto("Option A");
        given(eventsRepo.findById(eventId)).willReturn(Optional.ofNullable(event));

        // Act & Assert
        assertThatThrownBy(() -> optionService.createOption(requestDto, eventId, hackerId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Only the owner can add option to the event.");
        then(optionRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("GIVEN non-existent eventId WHEN create option THEN throw EventNotFoundException")
    void givenInvalidEventId_whenCreateOption_thenThrowEventNotFound(){
        // Arrange
        String invalidEventId = "nothing-123";
        String creatorId = "user-123";
        OptionRequestDto request = new OptionRequestDto("First Option");

        given(eventsRepo.findById(invalidEventId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> optionService.createOption(request,invalidEventId,creatorId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(invalidEventId);

        then(optionRepository).shouldHaveNoInteractions();
    }
}
