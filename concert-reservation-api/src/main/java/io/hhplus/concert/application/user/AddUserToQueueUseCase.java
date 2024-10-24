package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import org.springframework.stereotype.Service;

@Service
public class AddUserToQueueUseCase {

    private final UserService userService;
    private final UserQueueService userQueueService;

    public AddUserToQueueUseCase(UserQueueService userQueueService, UserService userService) {
        this.userQueueService = userQueueService;
        this.userService = userService;
    }

    public void execute(String userId) {
        if (userQueueService.isUserInExpiredQueue(userId)) {
            userQueueService.standbyUserQueueToken(userId);
        }

        if (!userQueueService.isUserInQueue(userId)) {
            userService.addUser(userId);
            userQueueService.addUserToQueue(userId);
        }
    }

}
