package tech.alexberbo.berboapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.model.Message;
import tech.alexberbo.berboapp.service.EventService;
import tech.alexberbo.berboapp.service.RoleService;
import tech.alexberbo.berboapp.service.UserService;

import java.util.Map;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user-event")
@RequiredArgsConstructor
public class UserEventController extends ExceptionHandling {
    private final EventService eventService;
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/get/{id}")
    ResponseEntity<HttpResponse> getUserEvent(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .message("User Event Retrieved!")
                        .reason(OK.getReasonPhrase())
                        .timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "event", eventService.getUserEvent(id),
                                "roles", roleService.getAllRoles()))
                        .build()
        );
    }

    @PostMapping("/report/{id}")
    ResponseEntity<HttpResponse> report(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id, @RequestBody Message message) {
        eventService.sendMessage(message, user.getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .message("Message sent! We will contact you per email: " + user.getEmail() +  " as soon as possible!")
                        .reason(CREATED.getReasonPhrase())
                        .timeStamp(now().toString())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "event", eventService.getUserEvent(id),
                                "roles", roleService.getAllRoles()))
                        .build()
        );
    }
}
