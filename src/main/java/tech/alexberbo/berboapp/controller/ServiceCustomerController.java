package tech.alexberbo.berboapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.model.ServiceCustomer;
import tech.alexberbo.berboapp.service.ServiceCustomerService;
import tech.alexberbo.berboapp.service.UserService;

import java.net.URI;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceCustomerController extends ExceptionHandling {
    private final ServiceCustomerService serviceCustomerService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getServices(@AuthenticationPrincipal UserDTO user,
                                                    Optional<Integer> page,
                                                    Optional<Integer> size) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Services retrieved!")
                        .data(of("user", userService.getUserByEmail(user.getEmail()),
                                "services", serviceCustomerService.services(page.orElse(0), size.orElse(10))))
                        .timeStamp(LocalTime.now().toString())
                        .build()
        );
    }

    @GetMapping("/new")
    public ResponseEntity<HttpResponse> newService(@AuthenticationPrincipal UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .data(Map.of("user", userService.getUserById(user.getId())))
                        .message("Add new service!")
                        .timeStamp(LocalTime.now().toString())
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createService(@AuthenticationPrincipal UserDTO user, @Valid @RequestBody ServiceCustomer serviceCustomer) {
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "service", serviceCustomerService.createService(serviceCustomer)))
                        .message("Service created!")
                        .timeStamp(now().toString())
                        .build()
        );
    }
}
