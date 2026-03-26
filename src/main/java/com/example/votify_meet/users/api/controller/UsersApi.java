package com.example.votify_meet.users.api.controller;

import com.example.votify_meet.users.api.dto.ChangePasswordRequestDto;
import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@Tag(name = "Users", description = "User Management APIs")
public interface UsersApi {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    UsersResponseDto getUser(@AuthenticationPrincipal Users user);

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    UsersResponseDto deleteUser(@AuthenticationPrincipal Users user);

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    UsersResponseDto updateUser(@AuthenticationPrincipal Users user, @Valid @RequestBody UpdateUsersRequestDto request);

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Void updatePassword( @AuthenticationPrincipal Users user, @Valid @RequestBody ChangePasswordRequestDto dto);
}
