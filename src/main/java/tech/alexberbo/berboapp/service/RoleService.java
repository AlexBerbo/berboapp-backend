package tech.alexberbo.berboapp.service;

import tech.alexberbo.berboapp.model.Role;

import java.util.Collection;

public interface RoleService {
    Role getUserRoleById(Long userId);
    Collection<Role> getAllRoles();
}
