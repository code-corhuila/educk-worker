package com.corhuila.edutrack.worker.infrastructure.amqp;

import com.corhuila.edutrack.worker.domain.model.GradeCreatedEvent;
import com.corhuila.edutrack.worker.domain.model.StudentAbsentEvent;
import com.corhuila.edutrack.worker.domain.port.in.ProcessNotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final ProcessNotificationUseCase processNotificationUseCase;

    public NotificationEventListener(ProcessNotificationUseCase processNotificationUseCase) {
        this.processNotificationUseCase = processNotificationUseCase;
    }

    @RabbitListener(queues = "grades.queue")
    public void onGradeCreated(String messagePayload) {
        log.info("Received AMQP message for GradeCreated: {}", messagePayload);
        GradeCreatedEvent event = new GradeCreatedEvent();
        event.setAggregateId(messagePayload);
        event.setPayload(new GradeCreatedEvent.GradePayload(messagePayload, "student-auto", 4.5));
        processNotificationUseCase.processGradeNotification(event);
    }

    @RabbitListener(queues = "attendance.queue")
    public void onStudentAbsent(String messagePayload) {
        log.info("Received AMQP message for StudentAbsent: {}", messagePayload);
        StudentAbsentEvent event = new StudentAbsentEvent();
        event.setAggregateId(messagePayload);
        event.setPayload(new StudentAbsentEvent.AbsentPayload(messagePayload, "student-auto", "ABSENT"));
        processNotificationUseCase.processAbsentNotification(event);
    }
}

// keywords: rabbitlistener gradecreated studentabsent template
