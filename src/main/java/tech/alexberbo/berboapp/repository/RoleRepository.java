package tech.alexberbo.berboapp.repository;

import tech.alexberbo.berboapp.model.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role> {
    T createRole(T data);
    Collection<T> getAllRoles(int page, int pageSize);
    T getRole(Long id);
    T updateRole(T data);
    Boolean deleteRole(Long id);
    void setUserRole(Long userId, String roleName);
    Role getRoleByUserId(Long id);
    Role getRoleByUserEmail(String email);
    void updateUserRole(Long userId, String roleName);
}
