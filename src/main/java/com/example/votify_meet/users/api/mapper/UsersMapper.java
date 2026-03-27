package com.example.votify_meet.users.api.mapper;

import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.domain.model.Users;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UsersMapper {
    public UsersResponseDto toResponse(Users entity) {
        return UsersResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Users toEntity(UsersRequestDto request) {
        return Users.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(request.password())
                .build();
    }


    private String normalize(String value){
        if(value != null && value.trim().isEmpty()){
            return null;
        }
        return value;
    }
    public void update(UpdateUsersRequestDto request, Users entity) {
        String firstName = normalize(request.firstName());
        if(firstName != null) {
            entity.setFirstName(firstName);
        }

        String lastName = normalize(request.lastName());
        if(lastName != null) {
            entity.setLastName(lastName);
        }

        String email = normalize(request.email());
        if(email != null) {
            entity.setEmail(email);
        }

        entity.setUpdatedAt(Instant.now());
    }
}
