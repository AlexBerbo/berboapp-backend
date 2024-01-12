package tech.alexberbo.berboapp.constant.query;

public interface EventQuery {
    String SELECT_EVENTS_BY_USER_ID = "SELECT ue.id, ue.device, ue.ip_address, e.type, e.description, ue.created_at FROM Events e JOIN UserEvents ue ON e.event_id = ue.event_id JOIN Users u ON u.user_id = ue.user_id WHERE u.user_id = :userId ORDER BY ue.created_at DESC LIMIT 10";
    String INSERT_USER_EVENT_BY_EMAIL_QUERY = "INSERT INTO UserEvents (user_id, event_id, device, ip_address) VALUES ((SELECT user_id FROM Users WHERE email = :email), (SELECT event_id FROM Events WHERE type = :type), :device, :ipAddress)";
    String SELECT_EVENT_BY_ID = "SELECT ue.id, ue.device, ue.ip_address, e.type, e.description, ue.created_at FROM Events e JOIN UserEvents ue ON e.event_id = ue.event_id WHERE ue.id = :id";
}
