package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CountUserQueueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CountUserQueueUseCase.class);

    private final UserQueueService userQueueService;

    public CountUserQueueUseCase(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Transactional(readOnly = true)
    public int execute(String userId) {
        logger.debug("Starting user count process in queue for userId: {}", userId);

        UserQueue userQueue = userQueueService.getUserQueueInfo(userId);
        int userCount = userQueueService.countUsersInQueue(userQueue.getCreateAt());

        logger.debug("User count for userId {} in queue: {}", userId, userCount);
        return userCount;
    }

}
