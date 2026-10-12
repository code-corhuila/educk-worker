package com.corhuila.edutrack.worker.application.service;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnProperty(name = "worker.jobs.attendance-timeout.enabled", havingValue = "true")
public class PeriodicJobRunnerService {
    private static final Logger log = LoggerFactory.getLogger(PeriodicJobRunnerService.class);
    private final RestTemplate restTemplate;
    private final URI endpoint;

    public PeriodicJobRunnerService(RestTemplate restTemplate,
            @Value("${worker.jobs.attendance-timeout.url}") String url) {
        this.restTemplate = restTemplate;
        this.endpoint = URI.create(url);
        if (endpoint.getHost() == null || endpoint.getUserInfo() != null
                || !("http".equals(endpoint.getScheme()) || "https".equals(endpoint.getScheme()))) {
            throw new IllegalArgumentException("An absolute HTTP(S) job URL without credentials is required");
        }
    }

    @Scheduled(fixedDelayString = "${worker.jobs.attendance-timeout.delay:300000}")
    public void executeAttendanceTimeoutJob() {
        try {
            var response = restTemplate.postForEntity(endpoint, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Attendance job was not acknowledged; retrying on the next schedule");
                return;
            }
            log.info("Attendance job acknowledged by the domain API");
        } catch (RestClientException error) {
            // Do not log remote response bodies, credentials or request URLs.
            log.warn("Attendance job unavailable; retrying on the next schedule ({})",
                    error.getClass().getSimpleName());
        }
    }
}
