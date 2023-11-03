package tech.alexberbo.berboapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.model.Customer;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.service.CustomerService;
import tech.alexberbo.berboapp.service.UserService;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController extends ExceptionHandling {
    private final CustomerService customerService;
    private final UserService userService;

    @GetMapping("/list")
    ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDTO user,
                                              @RequestParam Optional<Integer> page,
                                              @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .status(OK)
                        .statusCode(OK.value())
                        .data(Map.of("user", userService.getUserById(user.getId()),
                                "customer", customerService.getCustomers(page.orElse(0), size.orElse(10)),
                                "stats", customerService.getStats()))
                        .message("Customers retrieved!")
                        .timeStamp(now().toString())
                        .build()
        );
    }

    @GetMapping("/search")
    ResponseEntity<HttpResponse> searchCustomers(@AuthenticationPrincipal UserDTO user,
                                              @RequestParam Optional<String> name,
                                              @RequestParam Optional<Integer> page,
                                              @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok().body(
                createResponse(OK, user, customerService.searchCustomers(
                                name.orElse(""),
                                page.orElse(0),
                                size.orElse(10)), "Customers retrieved!")
        );
    }

    @PostMapping("/create")
    ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.created(URI.create("")).body(
                createResponse(CREATED, user, createdCustomer, "Customer created!")
        );
    }

    @PutMapping("/update")
    ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer) {
        Customer updateCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok().body(
                createResponse(OK, user, updateCustomer, "Customer updated!")
        );
    }

    @GetMapping("/get/{id}")
    ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok().body(
                createResponse(OK, user, customer, "Customer retrieved!")
        );
    }

    private HttpResponse createResponse(HttpStatus status, UserDTO user, Object customer, String message) {
        return HttpResponse.builder()
                .status(status)
                .statusCode(status.value())
                .data(Map.of("user", userService.getUserById(user.getId()),
                        "customer", customer))
                .message(message)
                .timeStamp(now().toString())
                .build();
    }
}
