package com.example.votify_meet.users.api.controller;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.service.UsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User Management APIs")
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/me")
    public ResponseEntity<UsersResponseDto> getUser(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(usersService.getUser(user.getId()));
    }
    @DeleteMapping("/me")
    public ResponseEntity<UsersResponseDto> deleteUser(@AuthenticationPrincipal Users user) {
        usersService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/me")
    public ResponseEntity<UsersResponseDto> updateUser(@AuthenticationPrincipal Users user, @Valid  @RequestBody UpdateUsersRequestDto request) {
        return ResponseEntity.ok(usersService.patchUser(user.getId(), request));
    }
}
