package tech.alexberbo.berboapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.model.Role;
import tech.alexberbo.berboapp.repository.RoleRepository;
import tech.alexberbo.berboapp.service.RoleService;

/**
    This is implementing the RoleService which is just calling the RoleRepository implementation where all the logic is actually done.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRepository;
    @Override
    public Role getUserRoleById(Long userId) {
        return roleRepository.getRoleByUserId(userId);
    }
}
