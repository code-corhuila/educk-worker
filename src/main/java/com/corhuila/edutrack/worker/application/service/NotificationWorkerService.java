package com.corhuila.edutrack.worker.application.service;

import com.corhuila.edutrack.worker.domain.model.GradeCreatedEvent;
import com.corhuila.edutrack.worker.domain.model.Notification;
import com.corhuila.edutrack.worker.domain.model.StudentAbsentEvent;
import com.corhuila.edutrack.worker.domain.port.in.ProcessNotificationUseCase;
import com.corhuila.edutrack.worker.domain.port.out.NotificationRepositoryPort;
import com.corhuila.edutrack.worker.domain.port.out.NotificationSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationWorkerService implements ProcessNotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificationWorkerService.class);

    private final NotificationRepositoryPort repositoryPort;
    private final NotificationSenderPort senderPort;

    public NotificationWorkerService(NotificationRepositoryPort repositoryPort, NotificationSenderPort senderPort) {
        this.repositoryPort = repositoryPort;
        this.senderPort = senderPort;
    }

    @Override
    @Transactional
    public void processGradeNotification(GradeCreatedEvent event) {
        log.info("Processing GradeCreated event: {}", event.getAggregateId());
        
        UUID studentId;
        try {
            studentId = UUID.fromString(event.getPayload().getStudentId());
        } catch (Exception e) {
            studentId = UUID.randomUUID();
        }

        String message = buildTemplate("Estimado Acudiente, el estudiante tiene una nueva calificación: %s", event.getPayload().getGrade().toString());

        Notification notification = new Notification(
            UUID.randomUUID(),
            studentId,
            "ACADEMIC",
            "Nueva Calificación Registrada",
            message,
            "IN_APP",
            "DELIVERED",
            LocalDateTime.now()
        );

        Notification saved = repositoryPort.save(notification);
        senderPort.dispatch(saved);
    }

    @Override
    @Transactional
    public void processAbsentNotification(StudentAbsentEvent event) {
        log.info("Processing StudentAbsent event: {}", event.getAggregateId());

        UUID studentId;
        try {
            studentId = UUID.fromString(event.getPayload().getStudentId());
        } catch (Exception e) {
            studentId = UUID.randomUUID();
        }

        String message = buildTemplate("Estimado Acudiente, el estudiante %s presenta una inasistencia el día de hoy.", event.getPayload().getStudentId());

        Notification notification = new Notification(
            UUID.randomUUID(),
            studentId,
            "ATTENDANCE",
            "Alerta de Inasistencia Registrada",
            message,
            "PUSH",
            "DELIVERED",
            LocalDateTime.now()
        );

        Notification saved = repositoryPort.save(notification);
        senderPort.dispatch(saved);
    }

    private String buildTemplate(String template, String... args) {
        return String.format(template, (Object[]) args);
    }
}
