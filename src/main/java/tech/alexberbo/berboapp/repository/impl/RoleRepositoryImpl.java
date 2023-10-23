package tech.alexberbo.berboapp.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tech.alexberbo.berboapp.exception.ApiException;
import tech.alexberbo.berboapp.model.Role;
import tech.alexberbo.berboapp.repository.RoleRepository;
import tech.alexberbo.berboapp.rowmapper.RoleRowMapper;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static tech.alexberbo.berboapp.constant.query.RoleQuery.*;

/**
    This is where the business logic for assigning roles is taking place, it is the same pattern as User class object.
    Using Jdbc template form jdbc driver dependency.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {
    private final NamedParameterJdbcTemplate jdbc;
    @Override
    public Role createRole(Role data) {
        return null;
    }

    @Override
    public Collection<Role> getAllRoles() {
        log.info("Fetching all roles!");
        try {
            return jdbc.query(SELECT_ROLES_QUERY, new RoleRowMapper());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An Error occurred please try again!");
        }
    }

    @Override
    public Role getRole(Long id) {
        return null;
    }

    @Override
    public void updateRole(Long userId, String roleName) {
        log.info("Updating user role!");
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
            jdbc.update(UPDATE_USER_ROLE_QUERY, Map.of("userId", userId, "roleId", Objects.requireNonNull(role).getId()));
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("User role by name not found: " + roleName);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An Error occurred!");
        }

    }

    @Override
    public Boolean deleteRole(Long id) {
        return null;
    }

    /**
        Role is being assigned to a user that is signing in, this method is called in the UserRepository implementation class.
        First we get the role that was passed as parameter, search for it, then we join tables User and Role to form a connection between User and Role.
     */
    @Override
    public void setUserRole(Long userId, String roleName) {
        log.info("Adding user role by user id: " + userId + " and role name: " + roleName);
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_INTO_USER_QUERY, Map.of("userId", userId, "roleId", Objects.requireNonNull(role).getId()));
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("User role not found");
        }
    }

    /**
        This one is a bit complicated, the query to be exact.
        The query is returning the role by user id, so we join all 3 tables and search for that user id, and then we get it back from the database.
     */
    @Override
    public Role getRoleByUserId(Long id) {
        log.info("Adding user role by user id: " + id);
        try {
            return jdbc.queryForObject(SELECT_ROLE_BY_USER_ID_QUERY, Map.of("userId", id), new RoleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("User role by user id not found: " + id);
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
