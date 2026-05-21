package com.example.votcha.auth.service;

import com.example.votcha.auth.api.dto.AuthRequestDto;
import com.example.votcha.auth.api.dto.RegisterResponseDto;
import com.example.votcha.auth.api.dto.AuthResponseDto;
import com.example.votcha.auth.domain.exception.UserNotVerifiedException;
import com.example.votcha.auth.domain.model.RefreshToken;
import com.example.votcha.common.notification.EmailService;
import com.example.votcha.votcha_search.api.dto.event.UserCreatedSyncEvent;
import com.example.votcha.users.api.dto.UsersRequestDto;
import com.example.votcha.users.api.mapper.UsersMapper;
import com.example.votcha.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepo  usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsersMapper usersMapper;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailService emailService;

    public RegisterResponseDto register(UsersRequestDto request){
        usersRepo.findByEmail(request.email()).ifPresent(user -> {
            throw new UserIsAlreadyExistsException(String.format("User with email %s already exists", request.email()));
        });

        Users user = usersMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        usersRepo.save(user);
        emailService.sendEmail(request, token);

        UserCreatedSyncEvent event = new UserCreatedSyncEvent(
                user.getId(),
                user.getFirstName() + " " +  user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
        eventPublisher.publishEvent(event);

        return new RegisterResponseDto("User registered successfully");
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

        if(!user.isVerified()){
            throw new UserNotVerifiedException("Please verify your email!");
        }

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

    public void verifyUser(String token) {
        Users verifiedUser = usersRepo.findByVerificationToken(token).orElseThrow(() -> new UserNotFoundException("User not found"));
        verifiedUser.setVerificationToken(null);
        verifiedUser.setVerified(true);
        usersRepo.save(verifiedUser);
    }
}
