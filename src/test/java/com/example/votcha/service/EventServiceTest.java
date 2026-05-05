package com.example.votcha.service;

import com.example.votcha.events.api.dto.EventDetailResponseDto;
import com.example.votcha.events.api.dto.EventRequestDto;
import com.example.votcha.events.api.dto.EventResponseDto;
import com.example.votcha.events.api.mapper.EventMapper;
import com.example.votcha.events.domain.exception.EventNotFoundException;
import com.example.votcha.events.domain.model.Event;
import com.example.votcha.events.domain.model.EventType;
import com.example.votcha.events.domain.model.Status;
import com.example.votcha.events.domain.repository.EventsRepo;
import com.example.votcha.events.service.EventService;
import com.example.votcha.options.api.dto.OptionRequestDto;
import com.example.votcha.options.api.dto.OptionResponseDto;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.redis.vote.service.VoteRedisService;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Role;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votcha_search.api.mapper.EventElasticMapper;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import com.example.votcha.votes.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock private EventsRepo eventsRepo;
    @Mock private UsersRepo usersRepo;
    @Mock private EventMapper eventMapper;
    @Mock private Users mockUser;
    @Mock VoteService voteService;
    @Mock EventElasticMapper eventElasticMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private VoteRedisService redisService;
    @InjectMocks private EventService eventService;


    @BeforeEach
    void setUp() {
        mockUser = Users.builder()
                .id("1")
                .email("john.doe@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .password("1111").build();
    }

    @Test
    @DisplayName("GIVEN valid request WHEN create event THEN return event with option list")
    void givenValidRequest_whenCreateEvent_thenReturnEventWithOptions(){
        // Arrange
        String userId = "u-123";


        List<OptionRequestDto> optRequests = Arrays.asList(OptionRequestDto.builder()
                .content("opt-1").build()
                , OptionRequestDto.builder()
                        .content("opt-2").build());
        List<OptionResponseDto> responses = Arrays.asList(
                OptionResponseDto.builder().content("opt-1").build(),
                OptionResponseDto.builder().content("opt-2").build()
        );
        List<Option> opts = Arrays.asList(
                Option.builder().content("opt-1").build(),
                Option.builder().content("opt-2").build()
        );

        EventRequestDto request = new EventRequestDto("Meet", "Desc", null, null, optRequests);
        Users user = Users.builder().id(userId).build();
        Event savedEvent = Event.builder()
                .id("e-123")
                .title("Meet")
                .creator(user)
                .options(opts)
                .type(EventType.STANDARD)
                .status(Status.OPEN)
                .build();

        EventResponseDto expectedResponse = new EventResponseDto("e-123", "Meet", "Desc", null, null, null, null, null, userId, responses);

        given(usersRepo.findById(userId)).willReturn(Optional.of(user));
        given(eventMapper.toEntity(request, user)).willReturn(savedEvent);
        given(eventsRepo.saveAndFlush(savedEvent)).willReturn(savedEvent);

        given(eventMapper.toResponse(eq(savedEvent), eq(opts))).willReturn(expectedResponse);

        // Act
        EventResponseDto actualResponse = eventService.createEvent(request, userId);

        // Assert
        assertThat(actualResponse.options()).isEqualTo(expectedResponse.options());
        assertThat(actualResponse.title()).isEqualTo("Meet");
    }

    @Test
    @DisplayName("GIVEN SURPRISED EventType WHEN map to response THEN description should be null")
    void givenSurprisedEventType_whenMapToResponse_thenDescriptionShouldBeNull(){
        // Arrange
        String userId = "u-123";
        Users user = Users.builder().id(userId).build();
        Event savedEvent = Event.builder()
                .id("e-123")
                .title("Surprise Party")
                .creator(user)
                .description("Dont return te description if it is surprised!")
                .type(EventType.SURPRISED)
                .build();

        // Act
        EventResponseDto expectedResponse = EventResponseDto.builder()
                .title("Surprise Party")
                .description(null).build();

        given(eventMapper.toResponse(eq(savedEvent), eq(Collections.emptyList()))).willReturn(expectedResponse);
        EventResponseDto response = eventMapper.toResponse(savedEvent, Collections.emptyList());

        // Assert
        assertThat(response.description()).isNull();
        assertThat(response.title()).isEqualTo("Surprise Party");
    }

    @Test
    @DisplayName("GIVEN STANDARD EventType WHEN map to response THEN description should not be null")
    void givenStandardEventType_whenMapToResponse_thenDescriptionShouldNotBeNull(){
        // Arrange
        String userId = "u-123";
        Users user = Users.builder().id(userId).build();
        Event savedEvent = Event.builder()
                .id("e-123")
                .title("Surprise Party")
                .creator(user)
                .description("Dont return te description if it is surprised!")
                .type(EventType.SURPRISED)
                .build();

        // Act
        EventResponseDto expectedResponse = EventResponseDto.builder()
                .title("Surprise Party")
                .description("Dont return te description if it is surprised!").build();

        given(eventMapper.toResponse(eq(savedEvent), eq(Collections.emptyList()))).willReturn(expectedResponse);
        EventResponseDto response = eventMapper.toResponse(savedEvent, Collections.emptyList());

        // Assert
        assertThat(response.description()).isEqualTo(savedEvent.getDescription());
        assertThat(response.title()).isEqualTo("Surprise Party");
    }
    @Test
    @DisplayName("GIVEN valid event and owner user WHEN get event detail THEN return event details with votes")
    void givenValidEventAndOwner_whenGetEventDetail_thenReturnEventDetails() {
        // Arrange
        String eventId = "e-123";
        Users owner = Users.builder().id("user-1").build();
        Event event = Event.builder()
                .id(eventId)
                .title("Team Meeting")
                .creator(owner)
                .build();


        List<VoteResponseDto> allVotes = Arrays.asList(VoteResponseDto.builder().id("v1").optionId("o1").build(), VoteResponseDto.builder().id("v2").optionId("o2").build(), VoteResponseDto.builder().id("v3").optionId("o3").build());

        EventDetailResponseDto expectedResponse = EventDetailResponseDto.builder()
                .id(eventId)
                .title("Team Meeting")
                .build();

        given(eventsRepo.findById(eventId)).willReturn(Optional.of(event));
        given(voteService.getEventVotes(eventId)).willReturn(allVotes);
        given(eventMapper.toDetailResponse(eq(event), any(Map.class))).willReturn(expectedResponse);

        // Act
        EventDetailResponseDto actualResponse = eventService.getEventDetail(owner, eventId);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(eventId);

        verify(eventsRepo).findById(eventId);
        verify(voteService).getEventVotes(eventId);
        verify(eventMapper).toDetailResponse(eq(event), any(Map.class));
    }

    @Test
    @DisplayName("GIVEN non-existent user WHEN create event THEN throw UserNotFoundException")
    void givenInvalidUser_whenCreateEvent_thenThrowException() {
        // Arrange
        String invalidUserId = "ghost-user";
        EventRequestDto request = new EventRequestDto("Parti", "Açıklama", null, null, Collections.emptyList());

        given(usersRepo.findById(invalidUserId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> eventService.createEvent(request, invalidUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(invalidUserId);
    }

    @Test
    @DisplayName("GIVEN non-existent eventId WHEN get event THEN trow EventNotFoundException")
    void givenInvalidEventId_whenGetEvent_thenThrowException(){
        // Arrange
        String invalidEventId = "ghost-event";


        // Assert:
        assertThatThrownBy(() -> eventService.getUserEvent(mockUser, invalidEventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(invalidEventId);
    }

    @Test
    @DisplayName("GIVEN valid eventId WHEN delete event THEN return event with empty option list")
    void givenValidEventId_whenDeleteEvent_thenReturnEventWithEmptyOptions(){
        // Arrange
        String eventId = "e-123";
        String userId = "u-123";
        Users user = Users.builder().id(userId).build();
        Event event = Event.builder().id(eventId).title("Meet").creator(user).build();

        EventResponseDto expectedResponse = new EventResponseDto(eventId,"Meet",null,null,null,null,null,null,userId,Collections.emptyList());

        // Act
        given(eventsRepo.findByIdAndCreator(eventId, mockUser)).willReturn(Optional.of(event));
        given(eventMapper.toResponse(eq(event),eq(Collections.emptyList()))).willReturn(expectedResponse);

        // Act
        EventResponseDto actualResponse = eventService.deleteUserEvent(mockUser, eventId);

        // Assert
        assertThat(actualResponse.options().isEmpty()).isTrue();
        assertThat(actualResponse.title()).isEqualTo("Meet");

        verify(eventsRepo).delete(event);
    }


}
