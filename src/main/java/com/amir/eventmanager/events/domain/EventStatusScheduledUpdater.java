package com.amir.eventmanager.events.domain;

import com.amir.eventmanager.events.api.EventStatus;
import com.amir.eventmanager.events.db.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@EnableScheduling
@Configuration
public class EventStatusScheduledUpdater {

    private final static Logger log = LoggerFactory.getLogger(EventStatusScheduledUpdater.class);

    private final EventRepository eventRepository;

    public EventStatusScheduledUpdater(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event.stats.cron}")
    public void updateEventStatuses() {
        log.info("EventStatusScheduledUpdater started");

        List<Long> startedEvents = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START);
        startedEvents.forEach(eventId ->
                eventRepository.changeEventStatus(eventId, EventStatus.STARTED)
        );

        List<Long> endedEvents = eventRepository.findEndedEventsWithStatus(EventStatus.STARTED);
        endedEvents.forEach(eventId ->
                eventRepository.changeEventStatus(eventId, EventStatus.FINISHED)
        );
    }

}
