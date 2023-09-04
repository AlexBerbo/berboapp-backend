package tech.alexberbo.berboapp.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.ApiException;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.service.AuthService;

import static tech.alexberbo.berboapp.exception.FilterExceptionHandler.handleExceptions;

/**
    This implementation is used to authenticate the user, it is used in the Controller class in the login method.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final HttpServletResponse response;
    @Override
    public Authentication authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return authentication;
        } catch (Exception e) {
            //handleExceptions(response, e);
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public UserDTO getAuthenticatedUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
