package io.hhplus.concert.application.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountUserQueueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CountUserQueueUseCase.class);

    private final UserQueueService userQueueService;

    public CountUserQueueUseCase(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Transactional(readOnly = true)
    public Long execute(String userId) {
        logger.debug("Starting user count process in queue for userId: {}", userId);

        Long userCount = userQueueService.countUsersInQueue(userId);

        logger.debug("User count for userId {} in queue: {}", userId, userCount);
        return userCount;
    }

}
