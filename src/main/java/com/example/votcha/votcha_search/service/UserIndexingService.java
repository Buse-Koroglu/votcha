package com.example.votcha.votcha_search.service;

import com.example.votcha.users.domain.model.Users;
import com.example.votcha.users.domain.repository.UsersRepo;
import com.example.votcha.votcha_search.api.dto.response.UserSyncResponse;
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
    private final UserElasticRepository userElasticRepository;

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
                    .map(user -> UserDocument.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .fullName(user.getFirstName() + " " + user.getLastName())
                                .createdAt(user.getCreatedAt())
                                .role(user.getRole())
                            .build()
                    ).toList();
            if(!documents.isEmpty()) {
                userElasticRepository.saveAll(documents);
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
