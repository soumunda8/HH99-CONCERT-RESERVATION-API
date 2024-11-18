package io.hhplus.concert.application.messaging;

import io.hhplus.concert.domain.messaging.redis.RedisPublisher;
import io.hhplus.concert.domain.user.RedisQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckAndPublishMessageUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CheckAndPublishMessageUseCase.class);

    private static final Long MAX_ACTIVE_USERS = 10L;

    private final RedisQueueRepository redisQueueRepository;
    private final RedisPublisher redisPublisher;

    public CheckAndPublishMessageUseCase(RedisQueueRepository redisQueueRepository, RedisPublisher redisPublisher) {
        this.redisQueueRepository = redisQueueRepository;
        this.redisPublisher = redisPublisher;
    }

    public void execute(String message) {
        Long activeUserCount = redisQueueRepository.countActiveUsers();

        if (activeUserCount < MAX_ACTIVE_USERS) {
            redisPublisher.publish(message);
            logger.info("Message published: {}", message);
        } else {
            logger.info("Not publishing message as active user count ({}) >= {}", activeUserCount, MAX_ACTIVE_USERS);
        }
    }

}