package io.hhplus.concert.application.user;

import org.springframework.stereotype.Service;

@Service
public class CheckUserStatusUseCase {

    private final UserQueueService userQueueService;

    public CheckUserStatusUseCase(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    public void execute(String userId) {
        if (!userQueueService.isUserActive(userId)) {
            throw new IllegalArgumentException("사용자가 활성 상태가 아닙니다.");
        }
    }

}
