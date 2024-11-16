package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.concert.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserQueueEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueEventListener.class);
    private final UserQueueService userQueueService;

    public UserQueueEventListener(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteRedisQueue(PaymentCompletedEvent event) {
        String userId = event.getUserId();
        userQueueService.removeUserQueueToken(userId);
        logger.info("User queue token removed for userId: {}", userId);
    }

}