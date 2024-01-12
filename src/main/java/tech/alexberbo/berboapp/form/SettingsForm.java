package tech.alexberbo.berboapp.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsForm {
    @NotNull(message = "This cannot be null or empty")
    private Boolean enabled;
    @NotNull(message = "This cannot be null or empty")
    private Boolean notLocked;
}
