package com.example.votcha.votcha_search.service;

import com.example.votcha.common.event.UserCreatedSyncEvent;
import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.domain.model.UserDocument;
import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSyncListener {
    private final UserElasticRepository userElasticRepository;

    /**
     * @EventListener: "Means I am listening this type of message".
     * When you throw UserCreatedSyncEvent anywhere, Spring automatically triggers this method
     */
    @EventListener
    @Async
    @ElasticSync(
            action = "USER_REGISTER_SYNC",
            index = "users",
            logDetails = "'Syncing user to ES: ' + #event.email"
    )
    public void handleUserCreatedEvent(UserCreatedSyncEvent event){
        UserDocument userDocument = UserDocument.builder()
                .id(event.id())
                .email(event.email())
                .fullName(event.fullName())
                .createdAt(event.createdAt())
                .role(event.role())
                .build();
        System.out.println("user ıs savıng");
        userElasticRepository.save(userDocument);
    }

}
