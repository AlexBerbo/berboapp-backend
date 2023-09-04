package tech.alexberbo.berboapp.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import tech.alexberbo.berboapp.model.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
    This maps the database result set into a java object.
 */
public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Role.builder()
                .id(resultSet.getLong("role_id"))
                .name(resultSet.getString("name"))
                .permissions(resultSet.getString("permission"))
                .build();
    }
}
