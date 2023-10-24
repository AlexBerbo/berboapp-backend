package tech.alexberbo.berboapp.listener;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tech.alexberbo.berboapp.event.NewUserEvent;
import tech.alexberbo.berboapp.service.EventService;

import static tech.alexberbo.berboapp.util.RequestUtils.getDevice;
import static tech.alexberbo.berboapp.util.RequestUtils.getIpAddress;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent userEvent) {
        log.info("New User Event is fired: {}", userEvent.getType().getDescription());
        eventService.addUserEvent(userEvent.getEmail(), userEvent.getType(), getDevice(request), getIpAddress(request));
    }
}
