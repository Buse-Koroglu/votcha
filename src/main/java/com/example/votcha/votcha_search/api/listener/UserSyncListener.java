package com.example.votcha.votcha_search.api.listener;

import com.example.votcha.votcha_search.api.dto.event.UserCreatedSyncEvent;
import com.example.votcha.votcha_search.api.dto.event.UserDeletedSyncEvent;
import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.api.mapper.UserElasticMapper;
import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSyncListener {
    private final UserElasticRepository userElasticRepo;
    private final UserElasticMapper  userElasticMapper;

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
        userElasticRepo.save(userElasticMapper.userCreatedSyncToUserDocument(event));
    }

    @EventListener
    @Async
    @ElasticSync(
            action = "USER_DELETED_SYNC",
            index = "users",
            logDetails = "'Deleting user from ES: ' + #event.id"
    )
    public void handleUserDeletedEvent(UserDeletedSyncEvent event){
        userElasticRepo.deleteById(event.id());
    }

}
