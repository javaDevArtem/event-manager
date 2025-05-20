package com.amir.eventmanager.events.domain;

import com.amir.eventmanager.events.api.*;
import com.amir.eventmanager.events.db.EventEntity;
import com.amir.eventmanager.events.db.EventRepository;
import com.amir.eventmanager.location.Location;
import com.amir.eventmanager.location.LocationService;
import com.amir.eventmanager.message.EventChangeKafkaMessage;
import com.amir.eventmanager.message.EventChangeMessageService;
import com.amir.eventmanager.message.EventFieldChange;
import com.amir.eventmanager.message.EventSender;
import com.amir.eventmanager.users.api.AuthenticationService;
import com.amir.eventmanager.users.domain.User;
import com.amir.eventmanager.users.domain.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class EventService {

    private final static Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final AuthenticationService authenticationService;
    private final EventEntityMapper entityMapper;
    private final EventSender eventSender;
    private final EventChangeMessageService eventChangeMessageService;

    public EventService(EventRepository eventRepository,
                        LocationService locationService,
                        AuthenticationService authenticationService,
                        EventEntityMapper entityMapper, EventSender eventSender,
                        EventChangeMessageService eventChangeMessageService) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.authenticationService = authenticationService;
        this.entityMapper = entityMapper;
        this.eventSender = eventSender;
        this.eventChangeMessageService = eventChangeMessageService;
    }

    public Event createEvent(EventCreateRequestDto createRequest) {

        Location location = locationService.getLocationById(createRequest.locationId());
        if (location.capacity() < createRequest.maxPlaces()) {
            throw new IllegalArgumentException("Capacity of location is: %s, but maxPlaces is: %s"
                    .formatted(location.capacity(), createRequest.maxPlaces()));
        }

        User currentUser = authenticationService.getCurrentAuthenticatedUser();

        var entity = new EventEntity(
                null,
                createRequest.name(),
                currentUser.id(),
                createRequest.maxPlaces(),
                List.of(),
                createRequest.date(),
                createRequest.cost(),
                createRequest.duration(),
                createRequest.locationId(),
                EventStatus.WAIT_START
        );

        entity = eventRepository.save(entity);

        log.info("New event was created: eventId={}", entity.getId());

        return entityMapper.toDomain(entity);
    }

    public Event getEventById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event entity wasn't found by id=%s"
                        .formatted(eventId)));

        return entityMapper.toDomain(event);
    }


    public void cancelEvent(Long eventId) {
        checkCurrentUserCanModifyEvent(eventId);
        Event event = getEventById(eventId);

        if (event.status().equals(EventStatus.CANCELLED)) {
            log.info("Event was already cancelled");
            return;
        }
        if (event.status().equals(EventStatus.FINISHED)
                || event.status().equals(EventStatus.STARTED)) {
            throw new IllegalArgumentException("Cannot cancel event with status: status=%s"
                    .formatted(event.status()));
        }

        eventRepository.changeEventStatus(eventId, EventStatus.CANCELLED);
        eventChangeMessageService.sendStatusChangeMessage(event, EventStatus.CANCELLED, 
            authenticationService.getCurrentAuthenticatedUser().id());
    }

    public Event updateEvent(Long eventId,
                             EventUpdateRequestDto updateRequest) {
        checkCurrentUserCanModifyEvent(eventId);
        EventEntity event = eventRepository.findById(eventId).orElseThrow();

        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot modify event in status: %s"
                    .formatted(event.getStatus()));
        }

        if (updateRequest.maxPlaces() != null || updateRequest.locationId() != null) {
            Long locationId = Optional.ofNullable(updateRequest.locationId())
                    .orElse(event.getLocationId());
            Integer maxPlaces = Optional.ofNullable(updateRequest.maxPlaces())
                    .orElse(event.getMaxPlaces());

            Location location = locationService.getLocationById(locationId);
            if (location.capacity() < maxPlaces) {
                throw new IllegalArgumentException(
                        "Capacity of location less than maxPlaces: capacity=%s, maxPlaces=%s"
                                .formatted(location.capacity(), maxPlaces)
                );
            }
        }

        if (updateRequest.maxPlaces() != null
                && event.getRegistrationList().size() > updateRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Registration count is more than maxPlaces: regCount=%s, maxPlaces=%s"
                            .formatted(event.getRegistrationList().size(), updateRequest.maxPlaces()));
        }

        Optional.ofNullable(updateRequest.name())
                .ifPresent(event::setName);
        Optional.ofNullable(updateRequest.maxPlaces())
                .ifPresent(event::setMaxPlaces);
        Optional.ofNullable(updateRequest.date())
                .ifPresent(event::setDate);
        Optional.ofNullable(updateRequest.cost())
                .ifPresent(event::setCost);
        Optional.ofNullable(updateRequest.duration())
                .ifPresent(event::setDuration);
        Optional.ofNullable(updateRequest.locationId())
                .ifPresent(event::setLocationId);

        eventRepository.save(event);
        Event updatedEvent = entityMapper.toDomain(event);
        
        EventChangeKafkaMessage changeMessage = new EventChangeKafkaMessage(
                event.getRegistrationList().stream().map(reg -> reg.getUserId()).toList(),
                updatedEvent.ownerId(),
                authenticationService.getCurrentAuthenticatedUser().id(),
                eventId
        );

        // Отслеживаем изменения полей
        if (updateRequest.name() != null && !updateRequest.name().equals(event.getName())) {
            EventFieldChange<String> nameChange = new EventFieldChange<>();
            nameChange.setOldField(event.getName());
            nameChange.setNewField(updateRequest.name());
            changeMessage.setName(nameChange);
        }

        if (updateRequest.maxPlaces() != null && !updateRequest.maxPlaces().equals(event.getMaxPlaces())) {
            EventFieldChange<Integer> maxPlacesChange = new EventFieldChange<>();
            maxPlacesChange.setOldField(event.getMaxPlaces());
            maxPlacesChange.setNewField(updateRequest.maxPlaces());
            changeMessage.setMaxPlaces(maxPlacesChange);
        }

        if (updateRequest.date() != null && !updateRequest.date().equals(event.getDate())) {
            EventFieldChange<LocalDateTime> dateChange = new EventFieldChange<>();
            dateChange.setOldField(event.getDate());
            dateChange.setNewField(updateRequest.date());
            changeMessage.setDate(dateChange);
        }

        if (updateRequest.cost() != null && !updateRequest.cost().equals(event.getCost())) {
            EventFieldChange<BigDecimal> costChange = new EventFieldChange<>();
            costChange.setOldField(BigDecimal.valueOf(event.getCost()));
            costChange.setNewField(BigDecimal.valueOf(updateRequest.cost()));
            changeMessage.setCost(costChange);
        }

        if (updateRequest.duration() != null && !updateRequest.duration().equals(event.getDuration())) {
            EventFieldChange<Integer> durationChange = new EventFieldChange<>();
            durationChange.setOldField(event.getDuration());
            durationChange.setNewField(updateRequest.duration());
            changeMessage.setDuration(durationChange);
        }

        if (updateRequest.locationId() != null && !updateRequest.locationId().equals(event.getLocationId())) {
            EventFieldChange<Integer> locationChange = new EventFieldChange<>();
            locationChange.setOldField(event.getLocationId() != null ? event.getLocationId().intValue() : null);
            locationChange.setNewField(updateRequest.locationId() != null ? updateRequest.locationId().intValue() : null);
            changeMessage.setLocationId(locationChange);
        }

        eventSender.sendEven(changeMessage);

        return updatedEvent;
    }


    private void checkCurrentUserCanModifyEvent(Long eventId) {
        User currentUser = authenticationService.getCurrentAuthenticatedUser();
        Event event = getEventById(eventId);

        if (!event.ownerId().equals(currentUser.id())
                && !currentUser.role().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("This user cannot modify this event");
        }
    }

    public List<Event> searchByFilter(EventSearchFilter searchFilter) {
        List<EventEntity>  foundEntities =  eventRepository.findEvents(
                searchFilter.name(),
                searchFilter.placesMin(),
                searchFilter.placesMax(),
                searchFilter.dateStartAfter(),
                searchFilter.dateStartBefore(),
                searchFilter.costMin(),
                searchFilter.costMax(),
                searchFilter.durationMin(),
                searchFilter.durationMax(),
                searchFilter.locationId(),
                searchFilter.eventStatus()
        );

        return foundEntities.stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public List<Event> getCurrentUserEvents() {
        User currentUser = authenticationService.getCurrentAuthenticatedUser();
        List<EventEntity> userEvents = eventRepository.findAllByOwnerIdIs(currentUser.id());

        return userEvents.stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}