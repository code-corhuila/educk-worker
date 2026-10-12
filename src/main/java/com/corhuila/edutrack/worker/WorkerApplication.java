package com.corhuila.edutrack.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.web.client.RestTemplate restTemplate(org.springframework.boot.web.client.RestTemplateBuilder builder) {
        return builder.setConnectTimeout(java.time.Duration.ofSeconds(2))
                .setReadTimeout(java.time.Duration.ofSeconds(10)).build();
    }
}
