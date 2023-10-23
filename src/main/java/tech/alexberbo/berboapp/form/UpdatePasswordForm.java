package tech.alexberbo.berboapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordForm {
    @NotNull(message = "Current password cannot be null or empty")
    private String currentPassword;
    @NotEmpty(message = "New password cannot be empty!")
    private String newPassword;
    @NotEmpty(message = "Confirmed password cannot be empty!")
    private String confirmPassword;
}
