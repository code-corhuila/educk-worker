package com.corhuila.edutrack.worker.domain.port.out;

import com.corhuila.edutrack.worker.domain.model.Notification;

public interface NotificationSenderPort {
    void dispatch(Notification notification);
}
