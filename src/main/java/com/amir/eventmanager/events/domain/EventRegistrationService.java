package com.amir.eventmanager.events.domain;

import com.amir.eventmanager.events.api.EventStatus;
import com.amir.eventmanager.events.db.EventEntity;
import com.amir.eventmanager.events.db.EventRegistrationEntity;
import com.amir.eventmanager.events.db.EventRegistrationRepository;
import com.amir.eventmanager.events.db.EventRepository;
import com.amir.eventmanager.users.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final EventEntityMapper entityMapper;

    public EventRegistrationService(EventRegistrationRepository registrationRepository,
                                    EventRepository eventRepository,
                                    EventService eventService,
                                    EventEntityMapper entityMapper) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.entityMapper = entityMapper;
    }

    public void registerUserOnEvent(
            User user,
            Long eventId
    ) {
        Event event = eventService.getEventById(eventId);

        if (user.id().equals(event.ownerId())) {
            throw new IllegalArgumentException("Owner cannot register on his event");
        }

        Optional<EventRegistrationEntity> registration = registrationRepository.findRegistration(user.id(), eventId);
        if (registration.isPresent()) {
            throw new IllegalArgumentException("User already registered on event");
        }

        if (!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot register on event with status=%s"
                    .formatted(event.status()));
        }

        registrationRepository.save(
                new EventRegistrationEntity(
                        null,
                        user.id(),
                        eventRepository.findById(eventId).orElseThrow()
                )
        );
    }

    public void cancelUserRegistration(
            User user,
            Long eventId
    ) {
        var event = eventService.getEventById(eventId);

        Optional<EventRegistrationEntity> registration = registrationRepository.findRegistration(user.id(), eventId);
        if (registration.isEmpty()) {
            throw new IllegalArgumentException("User have not registered on event");
        }

        if (!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot cancel registration on event with status=%s"
                    .formatted(event.status()));
        }

        registrationRepository.delete(registration.orElseThrow());
    }

    public List<Event> getUserRegisteredEvents(Long userId) {

        List<EventEntity> foundEvents = registrationRepository.findRegisteredEvents(userId);

        return foundEvents.stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}