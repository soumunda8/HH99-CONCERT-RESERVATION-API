package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.domain.user.UserQueueRepository;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import io.hhplus.concert.infrastructure.mapper.user.UserQueueMapper;
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
        List<UserQueueEntity> allStandbyUsers = userQueueRepository.getAllByUserStatusByCreateAt(
                QueueStatus.STANDBY.name(), LocalDateTime.now());

        for (UserQueueEntity userEntity : allStandbyUsers) {
            UserQueue userQueue = UserQueueMapper.toDomain(userEntity);
            List<UserQueueEntity> activeUsers = userQueueRepository.getAllByUserStatusByCreateAt(
                    QueueStatus.ACTIVE.name(), LocalDateTime.now());

            if (activeUsers.size() < 10) {
                userQueue.activate(LocalDateTime.now().plusMinutes(10));
                saveUserQueue(userQueue);
            }
        }
    }

    @Transactional
    public void expireUserQueues() {
        List<UserQueueEntity> expiredQueues = userQueueRepository.getTop10UserStatusByExpireAt(
                QueueStatus.ACTIVE.name(), LocalDateTime.now());

        for (UserQueueEntity queueEntity : expiredQueues) {
            UserQueue userQueue = UserQueueMapper.toDomain(queueEntity);
            userQueue.expire();
            saveUserQueue(userQueue);
        }
    }

    public void addUserToQueue(String userId) {
        UserQueue newUserQueue = new UserQueue(null, userId, QueueStatus.STANDBY, LocalDateTime.now(), null);
        saveUserQueue(newUserQueue);
    }

    public void standbyUserQueueToken(String userId) {
        UserQueue userQueue = getUserQueueInfo(userId);
        userQueue.standby();
        saveUserQueue(userQueue);
    }

    private void saveUserQueue(UserQueue userQueue) {
        UserQueueEntity userQueueEntity = UserQueueMapper.toEntity(userQueue);
        userQueueRepository.addUserQueue(userQueueEntity);
    }

    public void removeUserQueueToken(String userId) {
        try {
            UserQueue userQueue = getUserQueueInfo(userId);
            userQueueRepository.removeUserQueueToken(userQueue.getQueueId());
        } catch (Exception ex) {
            logger.error("Failed to remove user queue token for userId: {}", userId, ex);
            throw ex;
        }
    }

    public boolean isUserInExpiredQueue(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return userQueueRepository.getQueueInfo(userId)
                .map(queue -> QueueStatus.EXPIRE.name().equals(queue.getQueueStatus()) &&
                        queue.getQueueExpireAt().isBefore(now))
                .orElse(false);
    }

    public boolean isUserInQueue(String userId) {
        return userQueueRepository.getQueueInfo(userId).isPresent();
    }

    public boolean isUserActive(String userId) {
        UserQueue userQueue = getUserQueueInfo(userId);
        return userQueue.getQueueStatus() == QueueStatus.ACTIVE;
    }

    public boolean isValidQueueToken(Long queueId) {
        return userQueueRepository.getQueueInfoById(queueId)
                .map(userQueue -> userQueue.getQueueStatus().equals(QueueStatus.ACTIVE.name()) &&
                        userQueue.getQueueExpireAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public UserQueue getUserQueueInfo(String userId) {
        UserQueueEntity userQueueEntity = userQueueRepository.getQueueInfo(userId)
                .orElseThrow(() -> new IllegalArgumentException("관련한 정보가 없습니다."));
        return UserQueueMapper.toDomain(userQueueEntity);
    }

    public int countUsersInQueue(LocalDateTime createAt) {
        return userQueueRepository.countByQueue(createAt);
    }

}