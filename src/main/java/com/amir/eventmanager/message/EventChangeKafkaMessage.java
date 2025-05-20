package com.amir.eventmanager.message;

import com.amir.eventmanager.events.api.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EventChangeKafkaMessage {

    private Long eventId;
    private final List<Long> users;
    private final Long ownerId;
    private final Long changedById;

    public EventChangeKafkaMessage(List<Long> users, Long ownerId, Long changedById, Long eventId) {
        this.users = users;
        this.ownerId = ownerId;
        this.changedById = changedById;
        this.eventId = eventId;
    }

    private EventFieldChange<String> name;
    private EventFieldChange<Integer> maxPlaces;
    private EventFieldChange<LocalDateTime> date;
    private EventFieldChange<BigDecimal> cost;
    private EventFieldChange<Integer> duration;
    private EventFieldChange<Integer> locationId;
    private EventFieldChange<EventStatus> status;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<Long> getUsers() {
        return users;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getChangedById() {
        return changedById;
    }

    public EventFieldChange<String> getName() {
        return name;
    }

    public void setName(EventFieldChange<String> name) {
        this.name = name;
    }

    public EventFieldChange<Integer> getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(EventFieldChange<Integer> maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public EventFieldChange<LocalDateTime> getDate() {
        return date;
    }

    public void setDate(EventFieldChange<LocalDateTime> date) {
        this.date = date;
    }

    public EventFieldChange<BigDecimal> getCost() {
        return cost;
    }

    public void setCost(EventFieldChange<BigDecimal> cost) {
        this.cost = cost;
    }

    public EventFieldChange<Integer> getDuration() {
        return duration;
    }

    public void setDuration(EventFieldChange<Integer> duration) {
        this.duration = duration;
    }

    public EventFieldChange<Integer> getLocationId() {
        return locationId;
    }

    public void setLocationId(EventFieldChange<Integer> locationId) {
        this.locationId = locationId;
    }

    public EventFieldChange<EventStatus> getStatus() {
        return status;
    }

    public void setStatus(EventFieldChange<EventStatus> status) {
        this.status = status;
    }
}
