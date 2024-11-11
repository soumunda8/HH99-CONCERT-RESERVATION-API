package io.hhplus.concert.application.user.scheduler;

import io.hhplus.concert.application.user.UserQueueService;
import io.hhplus.concert.infrastructure.repository.user.RedisQueuePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserQueueScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueScheduler.class);

    private final RedisQueuePublisher redisQueuePublisher;

    public UserQueueScheduler(RedisQueuePublisher redisQueuePublisher) {
        this.redisQueuePublisher = redisQueuePublisher;
    }

    @Scheduled(fixedRate = 60000)
    public void execute() {
        try {
            redisQueuePublisher.publish("Test Message from UserService");
            logger.info("UserQueueScheduler completed execution successfully");
        } catch (Exception e) {
            logger.error("Error during UserQueueScheduler execution", e);
        }
    }

}