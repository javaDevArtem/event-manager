package com.amir.eventmanager.events.domain;

public record EventRegistration(
        Long id,
        Long userId,
        Long eventId
) {
}
