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
        logger.info("Starting UserQueueScheduler execution");

        expireQueues();
        activateStandbyUsers();

        logger.info("UserQueueScheduler execution completed");
    }

    private void expireQueues() {
        logger.info("Expiring user queues");

        try {
            userQueueService.expireUserQueues();
            logger.info("User queues expired successfully");
        } catch (Exception e) {
            logger.error("Error occurred while expiring user queues", e);
        }
    }

    private void activateStandbyUsers() {
        logger.info("Activating standby users");

        try {
            userQueueService.activateStandbyUsers();
            logger.info("Standby users activated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while activating standby users", e);
        }
    }
}