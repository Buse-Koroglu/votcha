package com.example.votify_meet.auth;

import com.example.votify_meet.config.JwtService;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.domain.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsersRepo  usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsersMapper usersMapper;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(UsersRequestDto request){
        usersRepo.findByEmail(request.email()).ifPresent(user -> {
            throw new UserIsAlreadyExistsException(String.format("User with email %s already exists", request.email()));
        });

        Users user = usersMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepo.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken, "User successfully registered");
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
        String jwtToken = jwtService.generateToken(user);
        return  new AuthResponseDto(jwtToken, "User successfully login");
    }
}
