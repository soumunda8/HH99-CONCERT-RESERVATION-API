package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.user.RedisQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisQueuePublisher {

    private static final Logger logger = LoggerFactory.getLogger(RedisQueuePublisher.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;
    private final RedisQueueRepository redisQueueRepository;

    private static final Long MAX_ACTIVE_USERS = 10L;

    public RedisQueuePublisher(RedisTemplate<String, String> redisTemplate, ChannelTopic topic, RedisQueueRepository redisQueueRepository) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
        this.redisQueueRepository = redisQueueRepository;
    }

    public void publish(String message) {

        Long activeUserCount = redisQueueRepository.countActiveUsers();

        if (activeUserCount < MAX_ACTIVE_USERS) {
            redisTemplate.convertAndSend(topic.getTopic(), message);
            logger.info("Published message from Redis: " + message + " from topic: " + topic.getTopic());
        } else {
            logger.info("Not publishing message as active user count ({}) >= {}", activeUserCount, MAX_ACTIVE_USERS);
        }

    }

}