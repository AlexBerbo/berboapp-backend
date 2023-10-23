package tech.alexberbo.berboapp.repository;

import tech.alexberbo.berboapp.model.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role> {
    T createRole(T data);
    Collection<T> getAllRoles();
    T getRole(Long id);
    void updateRole(Long id, String roleName);
    Boolean deleteRole(Long id);
    void setUserRole(Long userId, String roleName);
    Role getRoleByUserId(Long id);
    Role getRoleByUserEmail(String email);
    void updateUserRole(Long userId, String roleName);
}
