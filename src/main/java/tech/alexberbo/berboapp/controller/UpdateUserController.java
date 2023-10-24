package tech.alexberbo.berboapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.event.NewUserEvent;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.form.SettingsForm;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.service.EventService;
import tech.alexberbo.berboapp.service.RoleService;
import tech.alexberbo.berboapp.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static tech.alexberbo.berboapp.enumerator.EventType.*;
import static tech.alexberbo.berboapp.util.UserUtil.getAuthenticatedUser;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@CrossOrigin
public class UpdateUserController extends ExceptionHandling {
    private final RoleService roleService;
    private final UserService userService;
    private final EventService eventService;
    private final ApplicationEventPublisher publisher;

    /**
     * Here the user is being updated and its data changed when the user sends a request for it
     */
    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) {
        UserDTO userDTO = userService.updateUser(user);
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), PROFILE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .timeStamp(now().toString())
                        .reason(OK.getReasonPhrase())
                        .message("User updated!")
                        .developerMessage("Ez")
                        .data(Map.of("user", userService.getUserById(userDTO.getId()),
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .build());
    }

    @PatchMapping("/update-role/{roleName}")
    public ResponseEntity<HttpResponse> updateRole(Authentication authentication, @PathVariable("roleName") String roleName) {
        UserDTO user = getAuthenticatedUser(authentication);
        publisher.publishEvent(new NewUserEvent(user.getEmail(), ROLE_UPDATE));
        userService.updateRole(user.getId(), roleName);
        return ResponseEntity.ok().body(
                HttpResponse.builder().status(OK)
                        .statusCode(OK.value())
                        .message("Role updated successfully!")
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @PatchMapping("/update-settings")
    public ResponseEntity<HttpResponse> updateSettings(Authentication authentication, @RequestBody @Valid SettingsForm form) {
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updateSettings(user.getId(), form.getEnabled(), form.getNotLocked());
        publisher.publishEvent(new NewUserEvent(user.getEmail(), ACCOUNT_SETTINGS_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().status(OK)
                        .statusCode(OK.value())
                        .message("Account settings updated successfully!")
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @PatchMapping("/update-mfa")
    public ResponseEntity<HttpResponse> updateMfa(Authentication authentication) {
        UserDTO user = userService.updateMfa(getAuthenticatedUser(authentication).getEmail());
        publisher.publishEvent(new NewUserEvent(user.getEmail(), MFA_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder().status(OK)
                        .statusCode(OK.value())
                        .message("MFA updated successfully!")
                        .data(Map.of("user", user,
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .timeStamp(now().toString())
                        .build()
        );
    }

    /**
     * Backend call to the endpoint for updating the user's profile picture
     * Image is passed as a request parameter from the client as form data,
     * then it is being processed in the repository implementation class
     * */
    @PatchMapping("/update-image")
    public ResponseEntity<HttpResponse> updateImage(Authentication authentication, @RequestParam("image") MultipartFile image) {
        UserDTO user = getAuthenticatedUser(authentication);
        publisher.publishEvent(new NewUserEvent(user.getEmail(), PROFILE_PICTURE_UPDATE));
        userService.updateImage(user, image);
        return ResponseEntity.ok().body(
                HttpResponse.builder().status(OK)
                        .statusCode(OK.value())
                        .message("Profile image updated successfully!")
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "events", eventService.getUserEventsByUserId(user.getId()),
                                "roles", roleService.getAllRoles()))
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + ("/berbogram/images/"+ fileName)));
    }
}
