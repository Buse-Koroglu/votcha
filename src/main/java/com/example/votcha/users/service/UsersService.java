package com.example.votcha.users.service;

import com.example.votcha.votcha_search.api.dto.event.UserCreatedSyncEvent;
import com.example.votcha.votcha_search.api.dto.event.UserDeletedSyncEvent;
import com.example.votcha.users.api.dto.ChangePasswordRequestDto;
import com.example.votcha.users.api.dto.UpdateUsersRequestDto;
import com.example.votcha.users.api.dto.UsersResponseDto;
import com.example.votcha.users.api.mapper.UsersMapper;
import com.example.votcha.users.domain.exception.InvalidPasswordException;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepo  usersRepo;
    private final UsersMapper  usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UsersResponseDto getUser(String id) {
        return usersMapper.toResponse(usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id))));
    }

    public UsersResponseDto deleteUser(String id) {
        Users user = usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id)));
        usersRepo.delete(user);

        UserDeletedSyncEvent  event = new UserDeletedSyncEvent(id);
        eventPublisher.publishEvent(event);

        return usersMapper.toResponse(user);

    }


    public UsersResponseDto patchUser(String id, UpdateUsersRequestDto request) {
        Users user = usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id)));
        usersMapper.update(request, user);
        UsersResponseDto response =usersMapper.toResponse(usersRepo.save(user));

        UserCreatedSyncEvent event = new UserCreatedSyncEvent(
                user.getId(),
                user.getFirstName() + " " +  user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
        eventPublisher.publishEvent(event);


        return response;
    }

    @Transactional
    public void changePassword(String id, ChangePasswordRequestDto dto) {
        Users user = usersRepo.findById(id).orElseThrow( () -> new UserNotFoundException(String.format("User with id %s not found", id)));

        if(!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password does not match!");
        }
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
    }
}
