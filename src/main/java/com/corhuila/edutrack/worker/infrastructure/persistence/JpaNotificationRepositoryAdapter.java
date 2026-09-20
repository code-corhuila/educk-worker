package com.corhuila.edutrack.worker.infrastructure.persistence;

import com.corhuila.edutrack.worker.domain.model.Notification;
import com.corhuila.edutrack.worker.domain.port.out.NotificationRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JpaNotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final SpringDataNotificationRepository repository;

    public JpaNotificationRepositoryAdapter(SpringDataNotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
            notification.getId(),
            notification.getUserId(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getChannel(),
            notification.getStatus(),
            notification.getCreatedAt()
        );
        NotificationJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    private Notification toDomain(NotificationJpaEntity entity) {
        return new Notification(
            entity.getId(),
            entity.getUserId(),
            entity.getType(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getChannel(),
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }
}
