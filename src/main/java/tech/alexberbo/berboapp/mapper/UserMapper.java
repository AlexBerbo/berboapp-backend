package tech.alexberbo.berboapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.model.Role;
import tech.alexberbo.berboapp.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "permissions", source = "role.permissions")
    @Mapping(target = "id", source = "user.id")
    UserDTO toDTO(User user, Role role);
}
