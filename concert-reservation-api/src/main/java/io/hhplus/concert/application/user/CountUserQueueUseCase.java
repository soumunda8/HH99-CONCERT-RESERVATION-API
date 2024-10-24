package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CountUserQueueUseCase {

    private final UserQueueService userQueueService;

    public CountUserQueueUseCase(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Transactional(readOnly = true)
    public int execute(String userId) {
        UserQueue userQueue = userQueueService.getUserQueueInfo(userId);
        LocalDateTime createAt = userQueue.getCreateAt();
        return userQueueService.countUsersInQueue(createAt);
    }

}
