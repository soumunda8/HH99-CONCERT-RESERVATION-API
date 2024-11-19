package io.hhplus.concert.integration.kafka;

import io.hhplus.concert.domain.messaging.kafka.KafkaProducer;
import io.hhplus.concert.infrastructure.repository.messaging.kafka.KafkaProducerImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

public class KafkaProducerTest {

    @Test
    public void testSendMessage() {
        KafkaTemplate<String, String> kafkaTemplate = Mockito.mock(KafkaTemplate.class);

        KafkaProducer kafkaProducer = new KafkaProducerImpl(kafkaTemplate);

        String topic = "test-topic";
        String message = "테스트 메세지입니다.";

        kafkaProducer.sendMessage(topic, message);

        verify(kafkaTemplate).send(topic, message);
    }

}