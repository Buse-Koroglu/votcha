package com.example.votcha.users.scheduler;

import com.example.votcha.users.domain.repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Component
@Transactional
@Slf4j
public class UserCleanupScheduler {
    private final UsersRepo usersRepo;

    @Scheduled(cron = "0 */1 * * * *")
    public void cleanNonVerifiedUsersInFiveMinutes() {
        int cleaned =  usersRepo.deleteByIsVerifiedFalseAndCreatedAtBefore(Instant.now().minus(5, ChronoUnit.MINUTES));
        log.info("Cleaned users in five minutes for deletion: {}", cleaned);
    }
}
