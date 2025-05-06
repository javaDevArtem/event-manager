package com.amir.eventmanager.events.api;

import com.amir.eventmanager.events.domain.Event;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper {

    public EventDto toDto(Event event) {
        return new EventDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.registrationList().size(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }

}
