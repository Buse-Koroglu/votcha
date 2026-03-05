package com.example.votify_meet.users.api.controller;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.service.UsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User Management APIs")
public class UsersController {
    private final UsersService usersService;

    // todo - BURASI auth paketine taşındı, GÜNCELLENECEK
    @PostMapping("")
    public ResponseEntity<UsersResponseDto> createUser(@Valid @RequestBody UsersRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.createUser(request));

    }
    @GetMapping("/{id}")
    public ResponseEntity<UsersResponseDto> getUser(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(usersService.getUser(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<UsersResponseDto> deleteUser(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(usersService.deleteUser(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<UsersResponseDto> updateUser(@PathVariable(name = "id") String id, @Valid  @RequestBody UpdateUsersRequestDto request) {
        return ResponseEntity.ok(usersService.patchUser(id, request));
    }
}
