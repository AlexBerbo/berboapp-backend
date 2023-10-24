package tech.alexberbo.berboapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.event.NewUserEvent;
import tech.alexberbo.berboapp.exception.EmailDoesNotExistException;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.exception.PasswordResetCodeExpiredException;
import tech.alexberbo.berboapp.form.UpdatePasswordForm;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.service.EventService;
import tech.alexberbo.berboapp.service.RoleService;
import tech.alexberbo.berboapp.service.UserService;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;
import static tech.alexberbo.berboapp.enumerator.EventType.PASSWORD_UPDATE;
import static tech.alexberbo.berboapp.util.UserUtil.getAuthenticatedUser;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@CrossOrigin
public class PasswordController extends ExceptionHandling {
    private final UserService userService;
    private final RoleService roleService;
    private final EventService eventService;
    private final ApplicationEventPublisher publisher;

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
                                                      @RequestParam("url") String url) {
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

    @PatchMapping("/update-password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updatePassword(user.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmPassword());
        publisher.publishEvent(new NewUserEvent(user.getEmail(), PASSWORD_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().status(OK)
                        .statusCode(OK.value())
                        .message("Password updated successfully!")
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .timeStamp(now().toString())
                        .build()
        );
    }
    // END - Password Reset
}
