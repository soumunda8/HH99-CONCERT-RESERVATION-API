package io.hhplus.concert.application.user.scheduler;

import io.hhplus.concert.application.messaging.CheckAndPublishMessageUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserQueueScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueScheduler.class);

    private final CheckAndPublishMessageUseCase checkAndPublishMessageUseCase;


    public UserQueueScheduler(CheckAndPublishMessageUseCase checkAndPublishMessageUseCase) {
        this.checkAndPublishMessageUseCase = checkAndPublishMessageUseCase;
    }

    @Scheduled(fixedRate = 60000)
    public void execute() {
        checkAndPublishMessageUseCase.execute("Activate next user");
    }

}