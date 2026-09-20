package com.corhuila.edutrack.worker.infrastructure.notification;

import com.corhuila.edutrack.worker.domain.model.Notification;
import com.corhuila.edutrack.worker.domain.port.out.NotificationSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNotificationSenderAdapter implements NotificationSenderPort {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationSenderAdapter.class);

    @Override
    public void dispatch(Notification notification) {
        log.info("DISPATCHING NOTIFICATION [{}]: To user={}, title='{}', channel={}",
                 notification.getType(), notification.getUserId(), notification.getTitle(), notification.getChannel());
    }
}
