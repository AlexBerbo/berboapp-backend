package tech.alexberbo.berboapp.util;

import org.springframework.security.core.Authentication;
import tech.alexberbo.berboapp.dto.UserDTO;

public class UserUtil {
    public static UserDTO getAuthenticatedUser(Authentication authentication) {
        return ((UserDTO) authentication.getPrincipal());
    }
}
