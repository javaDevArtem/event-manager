package com.amir.eventmanager.events.api;

import com.amir.eventmanager.events.domain.Event;
import com.amir.eventmanager.events.domain.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final static Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final EventDtoMapper dtoMapper;

    public EventController(EventService eventService,
                           EventDtoMapper dtoMapper) {
        this.eventService = eventService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventCreateRequestDto eventCreateRequestDto
    ) {
        log.info("Get request for create new event: request={}", eventCreateRequestDto);
        Event createdEvent = eventService.createEvent(eventCreateRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoMapper.toDto(createdEvent));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for search event by id={}", eventId);

        Event event = eventService.getEventById(eventId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoMapper.toDto(event));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for cancel event with id={}", eventId);
        eventService.cancelEvent(eventId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid EventUpdateRequestDto updateRequestDto
    ) {
        log.info("Get request for update event: eventId={}, request={}", eventId, updateRequestDto);

        Event updatedEvent = eventService.updateEvent(eventId, updateRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoMapper.toDto(updatedEvent));
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(
            @RequestBody @Valid EventSearchFilter searchFilter
    ) {
        log.info("Get request for search events: filter={}", searchFilter);

        List<Event> foundEvents = eventService.searchByFilter(searchFilter);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundEvents.stream()
                        .map(dtoMapper::toDto)
                        .toList()
                );
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getUserEvents() {
        log.info("Get request for get all user events");
        List<Event> events = eventService.getCurrentUserEvents();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events.stream()
                        .map(dtoMapper::toDto)
                        .toList()
                );
    }

}