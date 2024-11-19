package io.hhplus.concert.integration.kafka;

import io.hhplus.concert.interfaces.messaging.kafka.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

public class KafkaConsumerServiceTest {

    @Test
    public void testConsumeMessage() {
        // Given
        KafkaConsumer consumerService = Mockito.spy(new KafkaConsumer());
        String testMessage = "Test message";

        // When
        consumerService.listen(testMessage);

        // Then
        verify(consumerService).listen(testMessage);
    }

}