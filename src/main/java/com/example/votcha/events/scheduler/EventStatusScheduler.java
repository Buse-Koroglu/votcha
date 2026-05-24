package com.example.votcha.events.scheduler;

import com.example.votcha.events.service.EventService;
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

    @Scheduled(cron = "0 */1 * * * *")
    public void setClosedForExpiredEvents(){eventService.closeExpiredEvents();}
}
