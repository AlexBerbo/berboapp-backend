package tech.alexberbo.berboapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
    This is just a login form that is used to make the code look a bit  nicer in the Login Controller
 */
@Getter
@Setter
public class LoginForm {
    @NotEmpty(message = "Email can't be empty!")
    @Email(message = "Please enter a valid Email!")
    private String email;
    @NotEmpty(message = "Password can't be empty!")
    private String password;
}
