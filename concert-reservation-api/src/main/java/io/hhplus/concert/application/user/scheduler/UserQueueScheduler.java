package io.hhplus.concert.application.user.scheduler;

import io.hhplus.concert.application.user.UserQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserQueueScheduler {

    private final UserQueueService userQueueService;

    public UserQueueScheduler(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Scheduled(fixedRate = 60000)
    public void execute() {
        expireQueues();
        activateStandbyUsers();
    }

    private void expireQueues() {
        userQueueService.expireUserQueues();
    }

    private void activateStandbyUsers() {
        userQueueService.activateStandbyUsers();
    }
}