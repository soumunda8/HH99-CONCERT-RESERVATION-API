package io.hhplus.concert.application.user.scheduler;

import io.hhplus.concert.application.user.UserQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserQueueScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueScheduler.class);

    private final UserQueueService userQueueService;

    public UserQueueScheduler(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Scheduled(fixedRate = 60000)
    public void execute() {
        try {
            expireQueues();
            activateStandbyUsers();
            logger.info("UserQueueScheduler completed execution successfully");
        } catch (Exception e) {
            logger.error("Error during UserQueueScheduler execution", e);
        }
    }

    private void expireQueues() {
        try {
            userQueueService.expireUserQueues();
            logger.debug("User queues expired successfully");
        } catch (Exception e) {
            logger.error("Error occurred while expiring user queues", e);
        }
    }

    private void activateStandbyUsers() {
        try {
            userQueueService.activateStandbyUsers();
            logger.debug("Standby users activated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while activating standby users", e);
        }
    }

}