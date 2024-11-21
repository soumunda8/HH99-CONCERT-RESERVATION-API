package io.hhplus.concert.interfaces.messaging.kafka;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "message-test-topic", groupId = "message-group")
    public void listen(String message) {
        System.out.println("Received Message from Kafka: " + message);
    }

}