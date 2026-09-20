package com.corhuila.edutrack.worker.domain.port.out;

import com.corhuila.edutrack.worker.domain.model.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    List<Notification> findByUserId(UUID userId);
}
