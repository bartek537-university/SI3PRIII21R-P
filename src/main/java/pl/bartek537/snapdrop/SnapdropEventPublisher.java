package pl.bartek537.snapdrop;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SnapdropEventPublisher {
    private static ApplicationEventPublisher publisher;

    public SnapdropEventPublisher(ApplicationEventPublisher publisher) {
        SnapdropEventPublisher.publisher = publisher;
    }

    public static void publish(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}
