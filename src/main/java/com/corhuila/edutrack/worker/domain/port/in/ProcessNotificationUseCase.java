package com.corhuila.edutrack.worker.domain.port.in;

import com.corhuila.edutrack.worker.domain.model.GradeCreatedEvent;
import com.corhuila.edutrack.worker.domain.model.StudentAbsentEvent;

public interface ProcessNotificationUseCase {
    void processGradeNotification(GradeCreatedEvent event);
    void processAbsentNotification(StudentAbsentEvent event);
}
