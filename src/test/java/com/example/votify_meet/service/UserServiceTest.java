package com.example.votify_meet.service;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import com.example.votify_meet.users.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UsersRepo usersRepo;

    @Mock
    private UsersMapper usersMapper;

    @InjectMocks
    private UsersService  usersService;


    @Test
    @DisplayName("GIVEN partial patch WHEN patch user THEN only requested fields should change")
    void givenPartialPatch_whenPatchUser_thenOnlyRequestedFieldsShouldChange(){
        // Arrange
        String userId = "u-123";

        Users existingUser = Users.builder().id(userId).firstName("John").lastName("Doe").email("doe@gmail.com").build();
        UpdateUsersRequestDto request = new UpdateUsersRequestDto("Kate", null, null);

        UsersResponseDto expectedResponse = new UsersResponseDto(userId, "Kate", "Doe", "doe@gmail.com", null, null);

        // Act
        given(usersRepo.findById(userId)).willReturn(Optional.of(existingUser));
        given(usersRepo.save(existingUser)).willReturn(existingUser);

        // Act
        willAnswer(invocation -> {
            Users userToUpdate = invocation.getArgument(1); // 2. parametre existingUser
            userToUpdate.setFirstName("Kate");
            return null; // void metot olduğu için null dönüyoruz
        }).given(usersMapper).update(request, existingUser);

        // Act
        given(usersMapper.toResponse(existingUser)).willReturn(expectedResponse);

        // Act
        UsersResponseDto actualResponse = usersService.patchUser(userId, request);

        // Assert
        assertThat(actualResponse.firstName()).isEqualTo("Kate");
        assertThat(actualResponse.lastName()).isEqualTo("Doe");
        assertThat(actualResponse.email()).isEqualTo("doe@gmail.com");

        assertThat(existingUser.getFirstName()).isEqualTo("Kate");
        assertThat(existingUser.getLastName()).isEqualTo("Doe");

        then(usersRepo).should().save(existingUser);
    }
}
