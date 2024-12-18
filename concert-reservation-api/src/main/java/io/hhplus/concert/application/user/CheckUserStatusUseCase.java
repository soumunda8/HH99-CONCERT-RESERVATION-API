package io.hhplus.concert.application.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckUserStatusUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CheckUserStatusUseCase.class);

    private final UserQueueService userQueueService;

    public CheckUserStatusUseCase(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    public void execute(String userId) {
        if (!userQueueService.isUserActive(userId)) {
            logger.error("User with userId: {} is not active", userId);
            throw new IllegalStateException("사용자가 활성 상태가 아닙니다.");
        }

        logger.info("User with userId: {} is active", userId);
    }

}