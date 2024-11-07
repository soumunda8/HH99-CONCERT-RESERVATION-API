package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.RedisQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;

@Service
public class UserQueueService {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueService.class);
    private final RedisQueueRepository redisQueueRepository;
    private static final Duration ACTIVE_USER_TTL = Duration.ofMinutes(10);
    private static final int TOP_NUM = 10;

    public UserQueueService(RedisQueueRepository redisQueueRepository) {
        this.redisQueueRepository = redisQueueRepository;
    }

    public void activateStandbyUsers() {
        Set<String> standbyUsers = redisQueueRepository.getTopUsersInWait(TOP_NUM);

        for (String userId : standbyUsers) {
            if (redisQueueRepository.getActiveUserInfo(userId) == null) {
                redisQueueRepository.setActive(userId, QueueStatus.ACTIVE.name(), ACTIVE_USER_TTL);
                redisQueueRepository.removeWait(userId);
                logger.info("User {} has been activated and moved from wait queue", userId);
            }
        }
    }

    @Transactional
    public void expireUserQueues() {
        Set<String> activeUsers = redisQueueRepository.getTopUsersInWait(TOP_NUM);

        for (String userId : activeUsers) {
            if (redisQueueRepository.getActiveUserInfo(userId) != null) {
                redisQueueRepository.deleteActiveUser(userId);
                logger.info("User {}'s active queue token has expired and removed", userId);
            }
        }
    }

    public void addUserToQueue(String userId) {
        double score = System.currentTimeMillis();
        redisQueueRepository.addWaitQueue(userId, score);
        logger.info("User {} added to wait queue with score {}", userId, score);
    }

    public void standbyUserQueueToken(String userId) {
        redisQueueRepository.addWaitQueue(userId, System.currentTimeMillis());
        logger.info("User {} moved to standby in wait queue", userId);
    }

    public void removeUserQueueToken(String userId) {
        try {
            redisQueueRepository.deleteActiveUser(userId);
            logger.info("User {} removed from wait queue", userId);
        } catch (Exception ex) {
            logger.error("Failed to remove user queue token for userId: {}", userId, ex);
            throw ex;
        }
    }

    public boolean isUserInExpiredQueue(String userId) {
        String activeStatus = redisQueueRepository.getActiveUserInfo(userId);
        return activeStatus != null && activeStatus.equals(QueueStatus.EXPIRE.name());
    }

    public boolean isUserInQueue(String userId) {
        return redisQueueRepository.getUserOrderInWait(userId) != null;
    }

    public boolean isUserActive(String userId) {
        String activeStatus = redisQueueRepository.getActiveUserInfo(userId);
        return activeStatus != null && activeStatus.equals(QueueStatus.ACTIVE.name());
    }

    public Long countUsersInQueue(String userId) {
        Long queueSize = redisQueueRepository.getUserOrderInWait(userId);
        return queueSize != null ? queueSize : 0L;
    }

}