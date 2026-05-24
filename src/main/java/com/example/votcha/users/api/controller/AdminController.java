package com.example.votcha.users.api.controller;

import com.example.votcha.common.logging.BusinessAction;
import com.example.votcha.users.api.dto.EmailRequest;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController implements AdminApi{
    private final AdminService adminService;

    @BusinessAction(action = "ADMIN_PROMOTED", domain = "ADMIN", logDetails = "'Operator ID: ' + #user.id + ' promoted user: ' + #request.email")
    @Override
    public ResponseEntity<Void> promoteToAdmin(Users user, @Valid EmailRequest request) {
        adminService.promoteToAdmin(request.email());
        return ResponseEntity.ok().build();
    }

    @BusinessAction(action = "ADMIN_DEMOTED", domain = "ADMIN", logDetails = "'Operator ID: ' + #user.id + ' demoted user: ' + #request.email")
    @Override
    public ResponseEntity<Void> demoteToUser(Users user, @Valid  EmailRequest request) {
        adminService.demoteToUser(request.email());
        return ResponseEntity.ok().build();
    }
}
