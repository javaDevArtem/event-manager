package com.amir.eventmanager.message;

import com.amir.eventmanager.events.api.EventStatus;
import com.amir.eventmanager.events.domain.Event;
import org.springframework.stereotype.Service;

@Service
public class EventChangeMessageService {
    
    private final EventSender eventSender;

    public EventChangeMessageService(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    public void sendStatusChangeMessage(Event event, EventStatus newStatus, Long changedById) {
        EventChangeKafkaMessage changeMessage = new EventChangeKafkaMessage(
                event.registrationList().stream().map(reg -> reg.userId()).toList(),
                event.ownerId(),
                changedById,
                event.id()
        );

        EventFieldChange<EventStatus> statusChange = new EventFieldChange<>();
        statusChange.setOldField(event.status());
        statusChange.setNewField(newStatus);
        changeMessage.setStatus(statusChange);

        eventSender.sendEven(changeMessage);
    }
} 