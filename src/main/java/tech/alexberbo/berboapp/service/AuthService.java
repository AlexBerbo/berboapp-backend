package tech.alexberbo.berboapp.service;

import org.springframework.security.core.Authentication;
import tech.alexberbo.berboapp.dto.UserDTO;

public interface AuthService {
    UserDTO authenticate(String email, String password);
    UserDTO getAuthenticatedUser(Authentication authentication);
}
