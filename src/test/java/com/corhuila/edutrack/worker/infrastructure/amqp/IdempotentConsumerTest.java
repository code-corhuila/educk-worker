package com.corhuila.edutrack.worker.infrastructure.amqp;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotentConsumerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private NotificationEventListener delegate;

    @Mock
    private Channel channel;

    private IdempotentConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new IdempotentConsumer(redisTemplate, delegate);
    }

    @Test
    void testConsumeGradeEvent_NewEvent_ShouldProcessAndAck() throws IOException {
        MessageProperties props = new MessageProperties();
        props.setMessageId("event-123");
        Message message = new Message("payload".getBytes(), props);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("event:dedup:event-123"), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(true);

        consumer.consumeGradeEvent(message, channel, 1L);

        verify(delegate).onGradeCreated("payload");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void testConsumeGradeEvent_DuplicateEvent_ShouldDiscardAndAck() throws IOException {
        MessageProperties props = new MessageProperties();
        props.setMessageId("event-123");
        Message message = new Message("payload".getBytes(), props);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("event:dedup:event-123"), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(false);

        consumer.consumeGradeEvent(message, channel, 1L);

        verify(delegate, never()).onGradeCreated(anyString());
        verify(channel).basicAck(1L, false);
    }

    @Test
    void testConsumeGradeEvent_ExceptionDuringProcessing_ShouldNack() throws IOException {
        MessageProperties props = new MessageProperties();
        props.setMessageId("event-123");
        Message message = new Message("payload".getBytes(), props);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("event:dedup:event-123"), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(true);

        doThrow(new RuntimeException("Simulated error")).when(delegate).onGradeCreated(anyString());

        consumer.consumeGradeEvent(message, channel, 1L);

        verify(delegate).onGradeCreated("payload");
        verify(channel).basicNack(1L, false, false);
    }
}
