package com.example.votify_meet.users.api.controller;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.service.UsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User Management APIs")
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDto getUser(@AuthenticationPrincipal Users user) {
        return usersService.getUser(user.getId());
    }
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDto deleteUser(@AuthenticationPrincipal Users user) {
        return usersService.deleteUser(user.getId());
    }
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UsersResponseDto updateUser(@AuthenticationPrincipal Users user, @Valid  @RequestBody UpdateUsersRequestDto request) {
        return usersService.patchUser(user.getId(), request);
    }
}
