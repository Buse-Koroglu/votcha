package com.example.votify_meet.auth;

import com.example.votify_meet.users.api.dto.UsersRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication Management APIs")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody UsersRequestDto request){

        AuthResponseDto response = authenticationService.register(request);
        // todo - XSS protection
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody AuthRequestDto request
    ){
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
