package com.corhuila.edutrack.worker.domain.model;

import java.time.LocalDateTime;

public class GradeCreatedEvent {
    private String eventId;
    private String eventType = "GradeCreated";
    private LocalDateTime occurredAt;
    private String correlationId;
    private String aggregateId;
    private GradePayload payload;

    public GradeCreatedEvent() {}

    public GradeCreatedEvent(String aggregateId, GradePayload payload) {
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
    public GradePayload getPayload() { return payload; }
    public void setPayload(GradePayload payload) { this.payload = payload; }

    public static class GradePayload {
        private String gradeId;
        private String studentId;
        private String studentName;
        private Double grade;

        public GradePayload() {}
        public GradePayload(String gradeId, String studentId, Double grade) {
            this.gradeId = gradeId;
            this.studentId = studentId;
            this.grade = grade;
        }

        public String getGradeId() { return gradeId; }
        public void setGradeId(String gradeId) { this.gradeId = gradeId; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public Double getGrade() { return grade; }
        public void setGrade(Double grade) { this.grade = grade; }
    }
}
