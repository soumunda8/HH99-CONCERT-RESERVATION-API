package io.hhplus.concert.domain.messaging.kafka;

public interface KafkaProducer {

    public void sendMessage(String topic, String message);

}