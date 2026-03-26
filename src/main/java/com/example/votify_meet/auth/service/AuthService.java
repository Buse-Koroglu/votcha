package com.example.votify_meet.auth.service;

import com.example.votify_meet.auth.api.dto.AuthRequestDto;
import com.example.votify_meet.auth.api.dto.RegisterResponseDto;
import com.example.votify_meet.auth.api.dto.AuthResponseDto;
import com.example.votify_meet.auth.domain.model.RefreshToken;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepo  usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsersMapper usersMapper;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public RegisterResponseDto register(UsersRequestDto request){
        usersRepo.findByEmail(request.email()).ifPresent(user -> {
            throw new UserIsAlreadyExistsException(String.format("User with email %s already exists", request.email()));
        });

        Users user = usersMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepo.save(user);
        return new RegisterResponseDto("User successfully registered");
    }

    public AuthResponseDto login(AuthRequestDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        Users user = usersRepo.findByEmail(request.email())
                .orElseThrow( () -> new UserNotFoundException(String.format("User with email %s not found", request.email())));
        String jwtToken = jwtService.generateToken(user); // for access token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user); // for refresh token
        return new AuthResponseDto(jwtToken, refreshToken.getToken(), "User successfully login");
    }

    @Transactional
    public AuthResponseDto refresh(String refreshToken) {
        var token = refreshTokenService.validateAndRotate(refreshToken);
        String accessToken = jwtService.generateToken(token.getUser());
        var newRefreshToken = refreshTokenService.createRefreshToken(token.getUser());

        return new AuthResponseDto(accessToken, newRefreshToken.getToken(), "Access token refreshed");
    }

    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.revokeByToken(refreshToken);
        }
    }
}
