package tech.alexberbo.berboapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.CodeExpiredException;
import tech.alexberbo.berboapp.exception.ExceptionHandling;
import tech.alexberbo.berboapp.model.HttpResponse;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.provider.JWTProvider;
import tech.alexberbo.berboapp.service.RoleService;
import tech.alexberbo.berboapp.service.UserService;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;
import static tech.alexberbo.berboapp.dtomapper.UserDTOMapper.toUser;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin
public class VerificationController extends ExceptionHandling {
    private final UserService userService;
    private final RoleService roleService;
    private final JWTProvider jwtProvider;

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

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())), roleService.getUserRoleById(user.getId()));
    }
}
