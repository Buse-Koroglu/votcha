package com.example.votcha.votcha_search.api.mapper;

import com.example.votcha.users.domain.model.Users;
import com.example.votcha.votcha_search.api.dto.event.UserCreatedSyncEvent;
import com.example.votcha.votcha_search.domain.model.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserElasticMapper {
    public UserDocument userToUserDocument(Users user) {
        return UserDocument.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();
    }
    public UserDocument userCreatedSyncToUserDocument(UserCreatedSyncEvent event){
        return UserDocument.builder()
                .id(event.id())
                .email(event.email())
                .fullName(event.fullName())
                .createdAt(event.createdAt())
                .role(event.role())
                .build();
    }
}
