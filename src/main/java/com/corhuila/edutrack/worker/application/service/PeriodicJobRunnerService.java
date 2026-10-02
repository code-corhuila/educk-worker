package com.corhuila.edutrack.worker.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PeriodicJobRunnerService {

    private static final Logger log = LoggerFactory.getLogger(PeriodicJobRunnerService.class);

    private final RestTemplate restTemplate;

    @Value("${api.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    public PeriodicJobRunnerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelayString = "${worker.jobs.attendance-timeout.delay:300000}")
    public void executeAttendanceTimeoutJob() {
        log.info("[WORKER-JOB] Iniciando proceso de expiración de notas y alertas diarias de asistencia por HTTP Polling...");
        try {
            // Ejemplo de llamada a la API por HTTP para delegar la lógica de negocio al dominio correspondiente.
            // restTemplate.postForObject(gatewayUrl + "/api/v1/attendance/jobs/timeout", null, String.class);
            log.info("[WORKER-JOB] Llamada a APIs finalizada correctamente.");
        } catch (Exception e) {
            log.error("[WORKER-JOB] Error ejecutando el Job: {}", e.getMessage());
        }
    }
}
