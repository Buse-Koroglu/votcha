package com.example.votify_meet.events.scheduler;

import com.example.votify_meet.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final EventService eventService;

    @Scheduled(cron = "0 */1 * * * *")
    public void runRevealSurpriseEventsJob(){
        eventService.revealExpiredSurpriseEvents();
    }
}
