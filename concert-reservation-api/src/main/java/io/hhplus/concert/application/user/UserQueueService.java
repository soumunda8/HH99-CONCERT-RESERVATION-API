package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.domain.user.UserQueueRepository;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserQueueService {

    private static final Logger logger = LoggerFactory.getLogger(UserQueueService.class);

    private final UserQueueRepository userQueueRepository;

    public UserQueueService(UserQueueRepository userQueueRepository) {
        this.userQueueRepository = userQueueRepository;
    }

    @Transactional
    public void activateStandbyUsers() {
        logger.info("Activating standby users.");

        List<UserQueueEntity> allStandbyUsers = userQueueRepository.getAllByUserStatusByCreateAt(QueueStatus.STANDBY.name(), LocalDateTime.now());

        for (UserQueueEntity user : allStandbyUsers) {
            List<UserQueueEntity> activeUsers = userQueueRepository.getAllByUserStatusByCreateAt(QueueStatus.ACTIVE.name(), LocalDateTime.now());

            if (activeUsers.size() < 10) {
                logger.debug("Activating user with queueId: {}", user.getQueueId());
                UserQueueEntity tenthUser = activeUsers.size() == 9 ? user : activeUsers.get(9);
                LocalDateTime expireAt = LocalDateTime.now().plusMinutes(10);
                UserQueueEntity updatedQueueEntity = UserQueueEntity.builder()
                        .queueId(tenthUser.getQueueId())
                        .userId(tenthUser.getUserId())
                        .queueStatus(QueueStatus.ACTIVE.name())
                        .queueExpireAt(expireAt)
                        .build();
                userQueueRepository.addUserQueue(updatedQueueEntity);
            }
        }

        logger.info("Standby users activated.");
    }

    @Transactional
    public void expireUserQueues() {
        logger.info("Expiring user queues.");

        List<UserQueueEntity> expiredQueues = userQueueRepository.getTop10UserStatusByExpireAt(QueueStatus.ACTIVE.name(), LocalDateTime.now());

        for (UserQueueEntity queue : expiredQueues) {
            logger.debug("Expiring queue for userId: {}", queue.getUserId());
            expireUserQueueToken(queue.getUserId());
        }

        logger.info("Expired queues processed.");
    }

    public void standbyUserQueueToken(String userId) {
        logger.info("Updating user queue status to STANDBY for userId: {}", userId);
        updateQueueStatus(userId, QueueStatus.STANDBY);
    }

    public void expireUserQueueToken(String userId) {
        logger.info("Expiring queue token for userId: {}", userId);
        updateQueueStatus(userId, QueueStatus.EXPIRE);
    }

    public UserQueue getUserQueueInfo(String userId) {
        logger.info("Fetching user queue information for userId: {}", userId);
        UserQueueEntity userQueue = userQueueRepository.getQueueInfo(userId)
                .orElseThrow(() -> {
                    logger.error("User queue information not found for userId: {}", userId);
                    return new IllegalArgumentException("관련한 정보가 없습니다.");
                });

        return convertToDomain(userQueue);
    }

    public UserQueue getUserQueueInfoById(Long queueId) {
        logger.info("Fetching user queue information for queueId: {}", queueId);
        UserQueueEntity userQueue = userQueueRepository.getQueueInfoById(queueId)
                .orElseThrow(() -> {
                    logger.error("User queue information not found for queueId: {}", queueId);
                    return new IllegalArgumentException("관련한 정보가 없습니다.");
                });

        return convertToDomain(userQueue);
    }

    public boolean isValidQueueToken(Long queueId) {
        logger.info("Fetching user queue information for queueId: {}", queueId);
        UserQueue userQueue = getUserQueueInfoById(queueId);

        // queueId가 존재하고, 상태가 유효하며, 만료되지 않았는지 확인
        return userQueue != null &&
                !userQueue.getQueueStatus().equals(QueueStatus.EXPIRE.name()) &&
                userQueue.getQueueExpireAt().isAfter(LocalDateTime.now());
    }

    public void updateQueueStatus(String userId, QueueStatus queueStatus) {
        logger.info("Updating queue status for userId: {} to {}", userId, queueStatus);
        UserQueue userQueue = getUserQueueInfo(userId);

        UserQueueEntity updatedQueueEntity = UserQueueEntity.builder()
                .queueId(userQueue.getQueueId())
                .queueStatus(queueStatus.name())
                .userId(userQueue.getUserId())
                .build();

        userQueueRepository.addUserQueue(updatedQueueEntity);
        logger.info("Queue status updated for userId: {}", userId);
    }

    public void addUserToQueue(String userId) {
        logger.info("Adding user to queue with userId: {}", userId);
        UserQueueEntity newUserQueue = UserQueueEntity.builder()
                .userId(userId)
                .queueStatus(QueueStatus.STANDBY.name())
                .build();
        userQueueRepository.addUserQueue(newUserQueue);
        logger.info("User added to queue successfully for userId: {}", userId);
    }

    public boolean isUserInExpiredQueue(String userId) {
        logger.debug("Checking if user with userId: {} is in expired queue", userId);
        return userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name());
    }

    public boolean isUserInQueue(String userId) {
        logger.debug("Checking if user with userId: {} is in queue", userId);
        return userQueueRepository.checkIfUserInQueue(userId);
    }

    public boolean isUserActive(String userId) {
        logger.debug("Checking if user with userId: {} is active", userId);

        UserQueue userQueue = getUserQueueInfo(userId);
        return userQueue.getQueueStatus().equals(QueueStatus.ACTIVE.name());
    }

    public int countUsersInQueue(LocalDateTime createAt) {
        logger.debug("Counting users in queue with createAt before {}", createAt);

        return userQueueRepository.countByQueue(createAt);
    }

    public void removeUserQueueToken(String userId) {
        logger.info("Removing user queue token for userId: {}", userId);

        UserQueue userQueue = getUserQueueInfo(userId);
        userQueueRepository.removeUserQueueToken(userQueue.getQueueId());
        logger.info("User queue token removed for userId: {}", userId);
    }

    private UserQueue convertToDomain(UserQueueEntity userQueueEntity) {
        UserQueue userQueue = new UserQueue();
        userQueue.setQueueId(userQueueEntity.getQueueId());
        userQueue.setQueueStatus(QueueStatus.valueOf(userQueueEntity.getQueueStatus()));
        userQueue.setUserId(userQueueEntity.getUserId());
        userQueue.setCreateAt(userQueueEntity.getCreateAt());
        userQueue.setQueueExpireAt(userQueueEntity.getQueueExpireAt());
        return userQueue;
    }

}
