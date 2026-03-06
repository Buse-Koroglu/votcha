package com.example.votify_meet.auth;

import com.example.votify_meet.users.api.dto.UsersRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication Management APIs")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(
            @Valid @RequestBody UsersRequestDto request){

        // todo - XSS protection
        return authenticationService.register(request);

    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDto authenticate(
            @RequestBody AuthRequestDto request
    ){
        return authenticationService.login(request);
    }
}
