package tech.alexberbo.berboapp.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.enumerator.EventType;
import tech.alexberbo.berboapp.event.NewUserEvent;
import tech.alexberbo.berboapp.exception.ApiException;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.service.AuthService;
import tech.alexberbo.berboapp.service.UserService;

import static tech.alexberbo.berboapp.enumerator.EventType.*;
import static tech.alexberbo.berboapp.exception.FilterExceptionHandler.handleExceptions;

/**
    This implementation is used to authenticate the user, it is used in the Controller class in the login method.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final HttpServletResponse response;
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @Override
    public UserDTO authenticate(String email, String password) {
        try {
            loginAttemptEvent(email, LOGIN_ATTEMPT);
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            loginAttemptSuccessEvent(getAuthenticatedUser(authentication), LOGIN_ATTEMPT_SUCCESS);
            return getAuthenticatedUser(authentication);
        } catch (Exception e) {
            loginAttemptEvent(email, LOGIN_ATTEMPT_FAILURE);
            handleExceptions(response, e);
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public UserDTO getAuthenticatedUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private void loginAttemptEvent(String email, EventType type) {
        if(null != userService.getUserByEmail(email)) {
            publisher.publishEvent(new NewUserEvent(email, type));
        }
    }

    private void loginAttemptSuccessEvent(UserDTO authenticatedUser, EventType type) {
        if(!authenticatedUser.isUsingMfa()) {
            publisher.publishEvent(new NewUserEvent(authenticatedUser.getEmail(), type));
        }
    }
}
