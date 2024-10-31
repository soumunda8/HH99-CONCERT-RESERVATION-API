package io.hhplus.concert.application.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddUserToQueueUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AddUserToQueueUseCase.class);

    private final UserService userService;
    private final UserQueueService userQueueService;

    public AddUserToQueueUseCase(UserQueueService userQueueService, UserService userService) {
        this.userQueueService = userQueueService;
        this.userService = userService;
    }

    @Transactional
    public void execute(String userId) {
        logger.info("Attempting to add user to queue with userId: {}", userId);

        // 만료된 큐에 있는 경우 대기 상태로 업데이트
        if (userQueueService.isUserInExpiredQueue(userId)) {
            userQueueService.standbyUserQueueToken(userId);
        }

        // 사용자가 큐에 없으면 추가
        if (!userQueueService.isUserInQueue(userId)) {
            userService.addUser(userId);
            userQueueService.addUserToQueue(userId);
            logger.info("User with userId: {} added to queue successfully", userId);
        }
    }

}
