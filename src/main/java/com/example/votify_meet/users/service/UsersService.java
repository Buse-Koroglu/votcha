package com.example.votify_meet.users.service;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final UsersRepo  usersRepo;
    private final UsersMapper  usersMapper;


    public UsersService(UsersRepo usersRepo, UsersMapper usersMapper) {
        this.usersRepo = usersRepo;
        this.usersMapper = usersMapper;
    }

    public UsersResponseDto createUser(UsersRequestDto request) {
        usersRepo.findByEmail(request.email()).ifPresent(user -> {
            throw new UserIsAlreadyExistsException(String.format("User with email %s already exists", request.email()));
        });
        return usersMapper.toResponse(usersRepo.save(usersMapper.toEntity(request)));
    }

    public UsersResponseDto getUser(String id) {
        return usersMapper.toResponse(usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id))));
    }
    public void deleteUser(String id) {
        Users user = usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id)));
        usersRepo.delete(user);

    }

    public UsersResponseDto patchUser(String id, UpdateUsersRequestDto request) {
        Users user = usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id)));
        usersMapper.update(request, user);
        return usersMapper.toResponse(usersRepo.save(user));
    }
}
