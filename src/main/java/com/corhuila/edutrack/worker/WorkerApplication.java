package com.corhuila.edutrack.worker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class WorkerApplication {
    private static final Logger log = LoggerFactory.getLogger(WorkerApplication.class);
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    @RabbitListener(queues = "${NOTIFICATIONS_QUEUE:notifications.queue}")
    public void receiveNotification(String payload) {
        log.info("Procesando evento en worker: {}", payload);
        // Implementa filtro de deduplicación idempotente
    }
}
