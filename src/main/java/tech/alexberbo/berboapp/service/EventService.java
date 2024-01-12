package tech.alexberbo.berboapp.service;

import tech.alexberbo.berboapp.enumerator.EventType;
import tech.alexberbo.berboapp.model.Message;
import tech.alexberbo.berboapp.model.UserEvent;

import java.util.Collection;

public interface EventService {
    Collection<UserEvent> getUserEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
    UserEvent getUserEvent(Long id);
    void sendMessage(Message message, String email);
}
