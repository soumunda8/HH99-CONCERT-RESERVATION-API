package io.hhplus.concert.infrastructure.repository.messaging.redis;

import io.hhplus.concert.domain.messaging.redis.RedisPublisher;
import io.hhplus.concert.domain.user.RedisQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisPublisherImpl implements RedisPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RedisPublisherImpl.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;

    public RedisPublisherImpl(RedisTemplate<String, String> redisTemplate, ChannelTopic topic, RedisQueueRepository redisQueueRepository) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        logger.info("Published message from Redis: " + message + " from topic: " + topic.getTopic());
    }

}