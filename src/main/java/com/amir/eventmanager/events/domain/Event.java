package com.amir.eventmanager.events.domain;

import com.amir.eventmanager.events.api.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Long id,

        String name,

        Long ownerId,

        int maxPlaces,

        List<EventRegistration> registrationList,

        LocalDateTime date,

        int cost,

        int duration,

        Long locationId,

        EventStatus status
) {

}