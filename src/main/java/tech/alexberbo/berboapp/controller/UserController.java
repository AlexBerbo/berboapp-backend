package tech.alexberbo.berboapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.*;
import tech.alexberbo.berboapp.form.LoginForm;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.model.User;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.provider.JWTProvider;
import tech.alexberbo.berboapp.service.AuthService;
import tech.alexberbo.berboapp.service.RoleService;
import tech.alexberbo.berboapp.service.UserService;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static tech.alexberbo.berboapp.constant.security.SecurityConstants.TOKEN_PREFIX;
import static tech.alexberbo.berboapp.dtomapper.UserDTOMapper.toUser;
import static tech.alexberbo.berboapp.util.UserUtil.getAuthenticatedUser;

/**
    This is a controller that controls what business logic will be executed when a request comes in.
    Which services and repositories are used for the corresponding endpoints
 */

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController extends ExceptionHandling {
    private final UserService userService;
    private final AuthService authService;
    private final JWTProvider jwtProvider;
    private final RoleService roleService;

    /**
        Login method, this method will receive email and password from the user,
        check if the user exists, map it to the DTO, if correct spring will let the user in
        and depending on whether the user uses MFA the corresponding logic will occur and execute.
        Assigning the user new JWToken for accessing the protected endpoints.
     */
    @PostMapping(path = "/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        Authentication authentication = authService.authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO user = authService.getAuthenticatedUser(authentication);
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
    }

    /**
        Register method that is receiving the desired inputs from the user, using the User Service and repository
        to check if the user is already registered in the database, if yes, exception to the user will be thrown.
        Registering the user data to the DB.
     */
    @PostMapping(path = "/register")
    public ResponseEntity<HttpResponse> register(@RequestBody @Valid User user) throws EmailExistsException {
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .timeStamp(now().toString())
                        .reason(CREATED.getReasonPhrase())
                        .message("User successfully registered")
                        .developerMessage("You made it bro")
                        .data(Map.of("user", userDTO))
                        .build());
    }

    /**
        This one retrieves a user that is currently logged in, by calling the authentication method and getting the username (email)
      */
    @GetMapping(path = "/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
        // TODO FIX ERROR
        UserDTO user = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message("Profile Retrieved")
                        .developerMessage("You made it bro!")
                        .data(Map.of("user", user))
                        .build());
    }

    /**
     * Here the user is being updated and its data changed when the user sends a request for it
     */
    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        UserDTO updatedUser = userService.updateUser(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message("User updated!")
                        .developerMessage("Ez")
                        .data(Map.of("user", user))
                        .build());
    }

    /**
        As the method name says, this method checks if the code that the email that sends the verification code is valid.
        If the code is good, user will get the access and refresh token so the user can access the protected urls.
     */
    @GetMapping(path = "/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) throws CodeExpiredException {
        UserDTO user = userService.verifyCode(email, code);
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
        Here we check if the header is there and if the token in that header is valid or not.
        If It's valid, we proceed and give the user a new token, so he can continue working on the app.
     */
    @GetMapping(path = "/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if(isHeaderAndTokenValid(request)) {
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
        Here we are verifying the account when the user registers on to the app.
        An email is sent to the user, and then when he gets this URL he confirms his account.
        If already confirmed, nothing will happen, user will just get a message that he is already confirmed and free to loginn.
     */
    @GetMapping(path = "/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message(userService.verifyAccount(key).isEnabled() ? "Account already verified" : "Account verified")
                        .developerMessage("You made it bro")
                        .build());
    }

    // START - Password Reset
    /**
        This sends an email to the user with a link that will lead him to the reset password functionality.
     */
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailDoesNotExistException {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Email for password recovery has been sent to: " + email)
                        .timeStamp(now().toString())
                        .build()
        );
    }

    /**
        After getting this link and clicking on it, the user will be redirected to the reset password page,
        but only if the key of the url is valid, checks are done in the UserRepository implementation.
     */
    @GetMapping("/verify/password/{url}")
    public ResponseEntity<HttpResponse> verifyVerificationURL(@PathVariable("url") String url) throws PasswordResetCodeExpiredException {
        UserDTO user = userService.verifyVerificationURL(url);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Enter new Password")
                        .data(Map.of("user", user))
                        .timeStamp(now().toString())
                        .build()
        );
    }

    /**
        Reset password post method, gets the new password twice, and checks for its equality,
        other checks and logic is done in the User Repository implementation
     */
    @PostMapping("/reset-password")
    public ResponseEntity<HttpResponse> renewPassword(@RequestParam("password") String password,
                                                      @RequestParam("confirmPassword") String confirmPassword,
                                                      @RequestParam("url") String url) throws PasswordResetCodeExpiredException {
        userService.renewPassword(url, password, confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Password reset successfully")
                        .timeStamp(now().toString())
                        .build()
        );
    }
    // END - Password Reset

    /**
        Presenting a white label error page when a user enters an url that does not exist.
     */
    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleErrorPage(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .message("There is no mapping for " + request.getMethod() + " request on this url")
                        .build()
        );
    }

    /**
     Checks to see if the authorization header is present or not, and if the token in the authorization header is valid
     */
    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
        Long userId = jwtProvider.getSubject(token, request);
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && jwtProvider.isTokenValid(token, userId);
    }

    /**
        Creates the userId URI
     */
    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    /**
        This method generates and sends the verification code to the user's email
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
         If the user is not using the MFA this response will be sent, and the user will get access and refresh token
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
        This method will return the user principal, it takes the user DTO as a parameter, and then that user DTO is
        converted into the real user using BeanUtils converter by user that is passed and the role of that user.
        That is all mapped in the DtoMapper package.
     */
    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())), roleService.getUserRoleById(user.getId()));
    }
}
