package com.example.votcha.votcha_search.service;

import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votcha_search.api.dto.response.UserSyncResponse;
import com.example.votcha.votcha_search.api.mapper.UserElasticMapper;
import com.example.votcha.votcha_search.domain.model.UserDocument;
import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIndexingService {
    private final UsersRepo  usersRepo;
    private final UserElasticRepository userElasticRepo;

    private final UserElasticMapper userElasticMapper;

    @Transactional(readOnly=true)
    public UserSyncResponse syncAllUsers() {
        int pageSize = 1000;
        int pageNumber = 0;
        long totalSynced = 0;
        long totalFound = usersRepo.count();

        Page<Users> usersPage;
        do {
            usersPage = usersRepo.findAll(PageRequest.of(pageNumber, pageSize));
            List<UserDocument> documents = usersPage.getContent().stream()
                    .map(userElasticMapper::userToUserDocument).toList();
            if(!documents.isEmpty()) {
                userElasticRepo.saveAll(documents);
                totalSynced += documents.size();
            }
            pageNumber++;
        }while(usersPage.hasNext());

        return UserSyncResponse.builder()
                .timestamp(Instant.now())
                .status("SUCCESS")
                .syncedCount(totalSynced)
                .totalDbCount(totalFound)
                .message("Bulk synchronization completed successfully.")
                .build();
    }

}
