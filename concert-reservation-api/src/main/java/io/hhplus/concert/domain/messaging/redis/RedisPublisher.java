package io.hhplus.concert.domain.messaging.redis;

public interface RedisPublisher {

    void publish(String message);

}