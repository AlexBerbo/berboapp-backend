package tech.alexberbo.berboapp.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import tech.alexberbo.berboapp.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserEventRowMapper implements RowMapper<UserEvent> {
    @Override
    public UserEvent mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return UserEvent.builder()
                .id(resultSet.getLong("id"))
                .type(resultSet.getString("type"))
                .description(resultSet.getString("description"))
                .ipAddress(resultSet.getString("ip_address"))
                .device(resultSet.getString("device"))
                .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
