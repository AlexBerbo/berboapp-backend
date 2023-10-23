package tech.alexberbo.berboapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateForm {
    @NotNull(message = "Id cannot be null or empty")
    private Long id;
    @NotEmpty(message = "First Name cannot be empty!")
    private String firstName;
    @NotEmpty(message = "Last Name cannot be empty!")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty!")
    @Email(message = "Invalid Email! Please enter a valid Email address!")
    private String email;
    private String phone;
    private String address;
    private String title;
    private String bio;
}
