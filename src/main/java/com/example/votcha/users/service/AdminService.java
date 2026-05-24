package com.example.votcha.users.service;

import com.example.votcha.users.domain.exception.AdminActionException;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Role;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UsersRepo usersRepo;

    @Transactional
    public void promoteToAdmin(String email) {
        Users user = usersRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
        if (user.getRole().equals(Role.USER)) {
            user.setRole(Role.ADMIN);
        }
    }

    @Transactional
    public void demoteToUser(String email) {
        Users user = usersRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));

        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new AdminActionException("You can't demote this administrator");
        }
        if (user.getRole().equals(Role.ADMIN)) {
            user.setRole(Role.USER);
        }

    }


}
