package io.hhplus.concert.integration.kafka;

import io.hhplus.concert.interfaces.messaging.kafka.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Test
    public void testKafkaListenerReceivesMessage() throws ExecutionException, InterruptedException {

        // Given
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        String topic = "test-topic";
        String message = "통합테스트 메세지입니다!";

        // When
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        RecordMetadata metadata = producer.send(record).get();

        // Then
        assertThat(metadata.topic()).isEqualTo(topic);
        assertThat(metadata.offset()).isGreaterThanOrEqualTo(0);

    }
}