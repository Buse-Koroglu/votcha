package com.example.votcha.users.api.controller;

import com.example.votcha.common.logging.BusinessAction;
import com.example.votcha.users.api.dto.ChangePasswordRequestDto;
import com.example.votcha.users.api.dto.UpdateUsersRequestDto;
import com.example.votcha.users.api.dto.UsersResponseDto;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {
    private final UsersService usersService;

    @Override
    public UsersResponseDto getUser(Users user) {
        return usersService.getUser(user.getId());
    }

    @BusinessAction(action = "USER_DELETED", domain = "USERS", logDetails = "'Target User ID: ' + #user.id")
    @Override
    public UsersResponseDto deleteUser(Users user) {
        return usersService.deleteUser(user.getId());
    }

    @BusinessAction(action = "USER_UPDATED", domain = "USERS", logDetails = "'Target User ID: ' + #user.id")
    @Override
    public UsersResponseDto updateUser(Users user, UpdateUsersRequestDto request) {
        return usersService.patchUser(user.getId(), request);
    }
    @BusinessAction(action = "PASSWORD_UPDATED", domain = "USERS", logDetails = "'Target User ID: ' + #user.id")
    @Override
    public Void updatePassword(Users user, ChangePasswordRequestDto dto) {
        usersService.changePassword(user.getId(), dto);
        return null;
    }


}
