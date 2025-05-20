package com.amir.eventmanager.events.domain;

import com.amir.eventmanager.events.api.EventStatus;
import com.amir.eventmanager.events.db.EventRepository;
import com.amir.eventmanager.message.EventChangeMessageService;
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
    private final EventService eventService;
    private final EventChangeMessageService eventChangeMessageService;

    public EventStatusScheduledUpdater(EventRepository eventRepository, 
                                     EventService eventService,
                                     EventChangeMessageService eventChangeMessageService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.eventChangeMessageService = eventChangeMessageService;
    }

    @Scheduled(cron = "${event.stats.cron}")
    public void updateEventStatuses() {
        log.info("EventStatusScheduledUpdater started");

        List<Long> startedEvents = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START);
        startedEvents.forEach(eventId -> {
            eventRepository.changeEventStatus(eventId, EventStatus.STARTED);
            var event = eventService.getEventById(eventId);
            eventChangeMessageService.sendStatusChangeMessage(event, EventStatus.STARTED, null);
        });

        List<Long> endedEvents = eventRepository.findEndedEventsWithStatus(EventStatus.STARTED);
        endedEvents.forEach(eventId -> {
            eventRepository.changeEventStatus(eventId, EventStatus.FINISHED);
            var event = eventService.getEventById(eventId);
            eventChangeMessageService.sendStatusChangeMessage(event, EventStatus.FINISHED, null);
        });
    }
}
