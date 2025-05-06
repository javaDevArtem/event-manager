package com.amir.eventmanager.events.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventSearchFilter(
        String name,
        Integer placesMin,
        Integer placesMax,
        LocalDateTime dateStartAfter,
        LocalDateTime dateStartBefore,
        BigDecimal costMin,
        BigDecimal costMax,
        Integer durationMin,
        Integer durationMax,
        Integer locationId,
        EventStatus eventStatus
) {}
