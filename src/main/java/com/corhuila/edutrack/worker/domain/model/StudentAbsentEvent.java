package com.corhuila.edutrack.worker.domain.model;

import java.time.LocalDateTime;

public class StudentAbsentEvent {
    private String eventId;
    private String eventType = "StudentAbsent";
    private LocalDateTime occurredAt;
    private String correlationId;
    private String aggregateId;
    private AbsentPayload payload;

    public StudentAbsentEvent() {}

    public StudentAbsentEvent(String aggregateId, AbsentPayload payload) {
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.occurredAt = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public AbsentPayload getPayload() { return payload; }
    public void setPayload(AbsentPayload payload) { this.payload = payload; }

    public static class AbsentPayload {
        private String attendanceId;
        private String studentId;
        private String studentName;
        private String status;

        public AbsentPayload() {}
        public AbsentPayload(String attendanceId, String studentId, String status) {
            this.attendanceId = attendanceId;
            this.studentId = studentId;
            this.status = status;
        }

        public String getAttendanceId() { return attendanceId; }
        public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
