package tech.alexberbo.berboapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

/**
    User class object.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class User {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    @NotEmpty(message = "First Name cannot be empty!")
    private String firstName;
    @NotEmpty(message = "Last Name cannot be empty!")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty!")
    @Email(message = "Invalid Email! Please enter a valid Email address!")
    private String email;
    @NotEmpty(message = "Password cannot be empty!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private boolean enabled;
    private boolean isNotLocked;
    private boolean isUsingMfa;
    private LocalDateTime createdAt;
}
