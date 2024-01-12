package tech.alexberbo.berboapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.EmailExistsException;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.form.LoginForm;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.model.User;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.provider.JWTProvider;
import tech.alexberbo.berboapp.service.AuthService;
import tech.alexberbo.berboapp.service.EventService;
import tech.alexberbo.berboapp.service.RoleService;
import tech.alexberbo.berboapp.service.UserService;

import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static tech.alexberbo.berboapp.constant.security.SecurityConstants.TOKEN_PREFIX;
import static tech.alexberbo.berboapp.dtomapper.UserDTOMapper.toUser;
import static tech.alexberbo.berboapp.util.UserUtil.getAuthenticatedUser;

/**
 * This is a controller that controls what business logic will be executed when a request comes in.
 * Which services and repositories are used for the corresponding endpoints
 */

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController extends ExceptionHandling {
    private final UserService userService;
    private final AuthService authService;
    private final EventService eventService;
    private final JWTProvider jwtProvider;
    private final RoleService roleService;

    /**
     * Login method, this method will receive email and password from the user,
     * check if the user exists, map it to the DTO, if correct spring will let the user in
     * and depending on whether the user uses MFA the corresponding logic will occur and execute.
     * Assigning the user new JWToken for accessing the protected endpoints.
     */
    @PostMapping(path = "/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        UserDTO user = authService.authenticate(loginForm.getEmail(), loginForm.getPassword());
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
    }

    /**
     * Register method that is receiving the desired inputs from the user, using the User Service and repository
     * to check if the user is already registered in the database, if yes, exception to the user will be thrown.
     * Registering the user data to the DB.
     */
    @PostMapping(path = "/register")
    public ResponseEntity<HttpResponse> register(@RequestBody @Valid User user) throws EmailExistsException {
        UserDTO userDTO = userService.register(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .timeStamp(now().toString())
                        .reason(CREATED.getReasonPhrase())
                        .message(String.format("You have successfully registered %s", user.getFirstName()))
                        .developerMessage("You made it bro")
                        .data(Map.of("user", userDTO))
                        .build());
    }

    /**
     * This one retrieves a user that is currently logged in, by calling the authentication method and getting the username (email)
     */
    @GetMapping(path = "/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        UserDTO user = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message("Profile Retrieved")
                        .developerMessage("You made it bro!")
                        .data(Map.of("user", user,
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .build());
    }

    /**
     * Here we check if the header is there and if the token in that header is valid or not.
     * If It's valid, we proceed and give the user a new token, so he can continue working on the app.
     */
    @GetMapping(path = "/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if (isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUserById(jwtProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .status(OK)
                            .statusCode(OK.value())
                            .timeStamp(now().toString())
                            .reason(OK.getReasonPhrase())
                            .message("Refresh Token")
                            .developerMessage("You made it bro")
                            .data(Map.of("user", user,
                                    "jwt_token", jwtProvider.createAccessToken(getUserPrincipal(user)),
                                    "refresh_token", token))
                            .build());
        } else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .timeStamp(now().toString())
                            .reason(BAD_REQUEST.getReasonPhrase())
                            .message("Refresh Token not valid")
                            .developerMessage("Refresh Token not valid")
                            .build());
        }
    }

    /**
     * Checks to see if the authorization header is present or not, and if the token in the authorization header is valid
     */
    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
        Long userId = jwtProvider.getSubject(token, request);
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && jwtProvider.isTokenValid(token, userId);
    }

    /**
     * Creates the userId URI
     */
    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    /**
     * This method generates and sends the verification code to the user's email
     */
    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
        userService.sendVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message("Verification code sent!")
                        .developerMessage("You made it bro")
                        .data(Map.of("user", user))
                        .build());

    }

    /**
     * If the user is not using the MFA this response will be sent, and the user will get access and refresh token
     */
    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message("Login successful")
                        .developerMessage("You made it bro")
                        .data(Map.of("user", user,
                                "jwt_token", jwtProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_token", jwtProvider.createRefreshAccessToken(getUserPrincipal(user))))
                        .build());
    }

    /**
     * This method will return the user principal, it takes the user DTO as a parameter, and then that user DTO is
     * converted into the real user using BeanUtils converter by user that is passed and the role of that user.
     * That is all mapped in the DtoMapper package.
     */
    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())), roleService.getUserRoleById(user.getId()));
    }
}
