package tech.alexberbo.berboapp.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tech.alexberbo.berboapp.enumerator.EventType;
import tech.alexberbo.berboapp.model.Message;
import tech.alexberbo.berboapp.model.UserEvent;
import tech.alexberbo.berboapp.repository.EventRepository;
import tech.alexberbo.berboapp.repository.MessageRepository;
import tech.alexberbo.berboapp.rowmapper.UserEventRowMapper;
import tech.alexberbo.berboapp.service.EmailService;

import java.util.Collection;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static tech.alexberbo.berboapp.constant.query.EventQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final EmailService emailService;
    private final MessageRepository messageRepository;
    @Override
    public Collection<UserEvent> getUserEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID, Map.of("userId", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType type, String device, String ipAddress) {
        jdbc.update(INSERT_USER_EVENT_BY_EMAIL_QUERY, Map.of("email", email, "type", type.name(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }

    @Override
    public UserEvent getUserEvent(Long id) {
        return jdbc.queryForObject(SELECT_EVENT_BY_ID, Map.of("id", id), new UserEventRowMapper());
    }

    @Override
    public void sendMessage(Message message, String email) {
        message.setCreatedAt(now());
        message.setUserEmail(email);
        messageRepository.save(message);
        emailService.sendReport(message, email);
    }
}
