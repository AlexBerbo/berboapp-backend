package tech.alexberbo.berboapp.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import tech.alexberbo.berboapp.enumerator.EventType;

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {
    private String email;
    private EventType type;
    public NewUserEvent(String email, EventType type) {
        super(email);
        this.email = email;
        this.type = type;
    }
}
