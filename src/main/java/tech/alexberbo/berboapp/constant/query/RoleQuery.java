package tech.alexberbo.berboapp.constant.query;

/**
    Queries that are executed in the Repository implementation package and classes
 */
public interface RoleQuery {
    String SELECT_ROLES_QUERY = "SELECT * FROM Roles ORDER BY role_id";
    String UPDATE_USER_ROLE_QUERY = "UPDATE UserRoles SET role_id = :roleId WHERE user_id = :userId";
    String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM Roles WHERE name = :roleName";
    String INSERT_ROLE_INTO_USER_QUERY = "INSERT INTO UserRoles (user_id, role_id) VALUES (:userId, :roleId)";
    String SELECT_ROLE_BY_USER_ID_QUERY = "SELECT r.role_id, r.name, r.permission FROM Roles r JOIN UserRoles ur ON ur.role_id = r.role_id JOIN Users u ON u.user_id = ur.user_id WHERE u.user_id = :userId";
}
