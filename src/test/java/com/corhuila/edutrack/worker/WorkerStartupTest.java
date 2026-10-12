package com.corhuila.edutrack.worker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import com.corhuila.edutrack.worker.application.service.PeriodicJobRunnerService;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkerStartupTest {
    @Autowired TestRestTemplate http;
    @Autowired ApplicationContext context;

    @Test void startsWithoutDatabaseOrUnconfiguredJob() {
        assertTrue(context.getBeansOfType(javax.sql.DataSource.class).isEmpty());
        assertTrue(context.getBeansOfType(PeriodicJobRunnerService.class).isEmpty());
        assertEquals(200, http.getForEntity("/api/v1/notifications/health", String.class)
                .getStatusCode().value());
    }
}
