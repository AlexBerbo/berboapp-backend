package tech.alexberbo.berboapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.enumerator.EventType;
import tech.alexberbo.berboapp.model.UserEvent;
import tech.alexberbo.berboapp.repository.EventRepository;
import tech.alexberbo.berboapp.service.EventService;

import java.util.Collection;
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    @Override
    public Collection<UserEvent> getUserEventsByUserId(Long userId) {
        return eventRepository.getUserEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        eventRepository.addUserEvent(email, eventType, device, ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
