# Stateless worker deployment

Implements the HTTP job-runner direction in ADR-009. No database, migrations,
RabbitMQ consumer, Redis deduplication or notification delivery is provided here.

Build and test with Java 21 and mvn verify, then run the packaged JAR.
The process listens on port 8084; GET /api/v1/notifications/health checks liveness.

Jobs are disabled by default. Enable ATTENDANCE_TIMEOUT_ENABLED=true only after
the owning domain publishes and approves an idempotent timeout-job contract.
Set ATTENDANCE_TIMEOUT_URL to that reachable HTTP(S) POST endpoint without
credentials in the URL. ATTENDANCE_TIMEOUT_DELAY defaults to 300000 ms.
The current Attendance API has no such endpoint: keep this job disabled there.
Do not treat worker liveness as evidence of successful business processing.

Connection/read timeouts are 2/10 seconds. A failed call leaves the process alive
and is retried on the next schedule. The domain must guarantee idempotency because
a timed-out request may already have completed. This change does not complete HU-002.
