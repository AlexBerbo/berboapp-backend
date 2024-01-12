package tech.alexberbo.berboapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordForm {
    @NotNull(message = "Id can't be null!")
    private Long userId;
    @NotEmpty(message = "New password can't be null!")
    private String newPassword;
    @NotEmpty(message = "Confirmed new password can't be null!")
    private String confirmNewPassword;
}
