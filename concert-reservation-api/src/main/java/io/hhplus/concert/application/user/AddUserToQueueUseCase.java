package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AddUserToQueueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AddUserToQueueUseCase.class);

    private final UserService userService;
    private final UserQueueService userQueueService;

    public AddUserToQueueUseCase(UserQueueService userQueueService, UserService userService) {
        this.userQueueService = userQueueService;
        this.userService = userService;
    }

    public void execute(String userId) {
        logger.info("Attempting to add user to queue with userId: {}", userId);

        // 만료된 큐에 있는지 확인 후 대기 상태로 변경
        if (userQueueService.isUserInExpiredQueue(userId)) {
            logger.info("User with userId: {} found in expired queue, updating to STANDBY", userId);
            userQueueService.standbyUserQueueToken(userId);
        }

        // 사용자가 큐에 없으면 새로 추가
        if (!userQueueService.isUserInQueue(userId)) {
            logger.info("User with userId: {} not found in queue, adding user to queue", userId);
            userService.addUser(userId);
            userQueueService.addUserToQueue(userId);
            logger.info("User with userId: {} successfully added to queue", userId);
        } else {
            logger.info("User with userId: {} already exists in queue", userId);
        }
    }

}
