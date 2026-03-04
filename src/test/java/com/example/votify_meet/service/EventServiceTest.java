package com.example.votify_meet.service;

import com.example.votify_meet.events.api.dto.EventRequestDto;
import com.example.votify_meet.events.api.dto.EventResponseDto;
import com.example.votify_meet.events.api.mapper.EventMapper;
import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.repository.EventsRepo;
import com.example.votify_meet.events.service.EventService;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock private EventsRepo eventsRepo;
    @Mock private OptionRepository optionRepo;
    @Mock private UsersRepo usersRepo;
    @Mock private EventMapper eventMapper;
    @InjectMocks private EventService eventService;

    @Test
    @DisplayName("GIVEN valid request WHEN create event THEN return event with empty option list")
    void givenValidRequest_whenCreateEvent_thenReturnEventWithEmptyOptions(){
        // GIVEN
        String userId = "u-123";
        EventRequestDto request = new EventRequestDto("Meet", "Desc", null, null);
        Users user = Users.builder().id(userId).build();
        Event savedEvent = Event.builder().id("e-123").title("Meet").creator(user).build();

        EventResponseDto expectedResponse = new EventResponseDto("e-123", "Meet", "Desc", null, null, null, null, null, userId, Collections.emptyList());

        given(usersRepo.findById(userId)).willReturn(Optional.of(user));
        given(eventMapper.toEntity(request, user)).willReturn(savedEvent);
        given(eventsRepo.saveAndFlush(savedEvent)).willReturn(savedEvent);

        given(eventMapper.toResponse(eq(savedEvent), eq(Collections.emptyList()))).willReturn(expectedResponse);

        // WHEN
        EventResponseDto actualResponse = eventService.createEvent(request, userId);

        // THEN
        assertThat(actualResponse.options()).isEmpty();
        assertThat(actualResponse.title()).isEqualTo("Meet");
    }

    @Test
    @DisplayName("GIVEN non-existent user WHEN create event THEN throw UserNotFoundException")
    void givenInvalidUser_whenCreateEvent_thenThrowException() {
        // GIVEN
        String invalidUserId = "ghost-user";
        EventRequestDto request = new EventRequestDto("Parti", "Açıklama", null, null);

        given(usersRepo.findById(invalidUserId)).willReturn(Optional.empty());

        // WHEN & THEN: Non-existent users cannot perform transactions!
        assertThatThrownBy(() -> eventService.createEvent(request, invalidUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(invalidUserId);
    }

    @Test
    @DisplayName("GIVEN non-existent eventId WHEN get event THEN trow EventNotFoundException")
    void givenInvalidEventId_whenGetEvent_thenThrowException(){
        // GIVEN
        String invalidEventId = "ghost-event";
        given(eventsRepo.findById(invalidEventId)).willReturn(Optional.empty());

        // WHEN & THEN:
        assertThatThrownBy(() -> eventService.getEvent(invalidEventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(invalidEventId);
    }

    @Test
    @DisplayName("GIVEN valid eventId WHEN delete event THEN return event with empty option list")
    void givenValidEventId_whenDeleteEvent_thenReturnEventWithEmptyOptions(){
        // GIVEN
        String eventId = "e-123";
        String userId = "u-123";
        Users user = Users.builder().id(userId).build();
        Event event = Event.builder().id(eventId).title("Community Event").creator(user).build();

        EventResponseDto expectedResponse = new EventResponseDto("e-123","Community Event",null,null,null,null,null,null,userId,Collections.emptyList());

        given(eventsRepo.findById(eventId)).willReturn(Optional.of(event));
        given(eventMapper.toResponse(eq(event),eq(Collections.emptyList()))).willReturn(expectedResponse);

        // WHEN
        EventResponseDto actualResponse = eventService.deleteEvent(eventId);

        // THEN
        assertThat(actualResponse.options().isEmpty());
        assertThat(actualResponse.title()).isEqualTo("Community Event");

        verify(eventsRepo).deleteById(eventId);
    }

}
