package com.corhuila.edutrack.worker.infrastructure.amqp;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class IdempotentConsumer {

    private static final Logger log = LoggerFactory.getLogger(IdempotentConsumer.class);

    private final StringRedisTemplate redisTemplate;
    private final NotificationEventListener delegate;

    public IdempotentConsumer(StringRedisTemplate redisTemplate, NotificationEventListener delegate) {
        this.redisTemplate = redisTemplate;
        this.delegate = delegate;
    }

    @RabbitListener(queues = "${rabbitmq.queue.grade:academic.grade.queue}", ackMode = "MANUAL")
    public void consumeGradeEvent(Message message, Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        processMessage(message, channel, tag, delegate::onGradeCreated);
    }

    @RabbitListener(queues = "${rabbitmq.queue.attendance:attendance.absent.queue}", ackMode = "MANUAL")
    public void consumeAttendanceEvent(Message message, Channel channel,
                                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        processMessage(message, channel, tag, delegate::onStudentAbsent);
    }

    private void processMessage(Message message, Channel channel, long tag,
                                Consumer<String> eventProcessor) throws IOException {
        String eventId = message.getMessageProperties().getMessageId();
        if (eventId == null || eventId.isEmpty()) {
            log.warn("Message received without messageId. Using payload hash as fallback.");
            eventId = java.util.UUID.nameUUIDFromBytes(message.getBody()).toString();
        }

        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent("event:dedup:" + eventId, "PROCESSING", Duration.ofHours(24));

        if (Boolean.FALSE.equals(isNew)) {
            log.warn("Duplicate event detected: {}. Discarding immediately.", eventId);
            channel.basicAck(tag, false);
            return;
        }

        try {
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);
            eventProcessor.accept(payload);
            
            // Mark as COMPLETED after successful processing
            redisTemplate.opsForValue().set("event:dedup:" + eventId, "COMPLETED", Duration.ofHours(24));
            channel.basicAck(tag, false);
            log.info("Event {} processed successfully.", eventId);
        } catch (Exception e) {
            log.error("Error processing event: {}", eventId, e);
            // Delete the key so it can be retried later
            redisTemplate.delete("event:dedup:" + eventId);
            // Requeue = false -> Envía el mensaje a la Dead Letter Queue (DLQ)
            channel.basicNack(tag, false, false);
        }
    }
}
