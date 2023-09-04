package tech.alexberbo.berboapp.dtomapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.model.Role;
import tech.alexberbo.berboapp.model.User;

@Component
public class UserDTOMapper {
    /**
        This transfers the user data to user DTO
     */
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    /**
        This transfers the user data to user DTO, but this one also sets the permissions and roles
     */
    public static UserDTO fromUser(User user, Role role) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(role.getName());
        userDTO.setPermissions(role.getPermissions());
        return userDTO;
    }

    /**
        This transfers the userDTO data to user
     */
    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
