package com.corhuila.edutrack.worker.infrastructure.web;

import com.corhuila.edutrack.worker.domain.model.Notification;
import com.corhuila.edutrack.worker.domain.port.out.NotificationRepositoryPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepositoryPort repositoryPort;

    public NotificationController(NotificationRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(repositoryPort.findByUserId(userId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK - Worker Notifications (HU-002)");
    }
}
