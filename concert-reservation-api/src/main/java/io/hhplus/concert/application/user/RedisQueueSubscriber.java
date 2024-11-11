package io.hhplus.concert.application.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisQueueSubscriber implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisQueueSubscriber.class);

    private final UserQueueService userQueueService;

    public RedisQueueSubscriber(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String channel = new String(pattern);
        String messageBody = new String(message.getBody());

        logger.info("Received message from Redis: " + messageBody + " from channel: " + channel);
        userQueueService.activateStandbyUsers();

    }

}