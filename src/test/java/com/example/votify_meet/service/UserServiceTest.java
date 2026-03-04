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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    private UsersRepo usersRepo;
    @Spy
    private UsersMapper usersMapper = new UsersMapper();
    @InjectMocks
    private UsersService usersService;

    @Test
    @DisplayName("GIVEN partial patch WHEN patch user THEN only requsted fields should change")
    void givenPartialPatch_whenPatchUser_thenOnlyRequestedFieldsShouldChange(){
        // GIVEN
        String userId = "u-123";

        Users existingUser = Users.builder().id(userId).firstName("John").lastName("Doe").email("doe@gmail.com").build();

        UpdateUsersRequestDto request = new UpdateUsersRequestDto("Kate",null,null,null);
        given(usersRepo.findById(userId)).willReturn(Optional.of(existingUser));
        given(usersRepo.save(existingUser)).willReturn(existingUser);

        // WHEN
        UsersResponseDto patchedUser = usersService.patchUser(userId,request);
        // THEN
        assertThat(existingUser.getFirstName()).isEqualTo("Kate");
        assertThat(existingUser.getLastName()).isEqualTo("Doe");
        assertThat(existingUser.getEmail()).isEqualTo("doe@gmail.com");
        verify(usersRepo).save(existingUser);
    }
}
