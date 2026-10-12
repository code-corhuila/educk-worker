package com.corhuila.edutrack.worker.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class PeriodicJobRunnerServiceTest {
    @Test void recoversOnNextRunAfterDomainFailure() {
        var http = new RestTemplate();
        var server = MockRestServiceServer.bindTo(http).build();
        String endpoint = "http://domain.test/jobs/timeout";
        server.expect(requestTo(endpoint)).andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());
        server.expect(requestTo(endpoint)).andExpect(method(HttpMethod.POST))
                .andRespond(withNoContent());
        var runner = new PeriodicJobRunnerService(http, endpoint);
        assertDoesNotThrow(runner::executeAttendanceTimeoutJob);
        assertDoesNotThrow(runner::executeAttendanceTimeoutJob);
        server.verify();
    }

    @Test void refusesMissingOrCredentialBearingEndpoint() {
        for (String endpoint : new String[]{"", "/jobs", "file:///tmp/job", "http://user:pass@domain.test/job"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new PeriodicJobRunnerService(new RestTemplate(), endpoint));
        }
    }
}
