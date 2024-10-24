package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.domain.user.UserQueueRepository;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserQueueService {

    private final UserQueueRepository userQueueRepository;

    public UserQueueService(UserQueueRepository userQueueRepository) {
        this.userQueueRepository = userQueueRepository;
    }

    @Transactional
    public void activateStandbyUsers() {
        List<UserQueueEntity> allStandbyUsers = userQueueRepository.getAllByUserStatusByCreateAt(QueueStatus.STANDBY.name(), LocalDateTime.now());

        for (UserQueueEntity user : allStandbyUsers) {
            List<UserQueueEntity> activeUsers = userQueueRepository.getAllByUserStatusByCreateAt(QueueStatus.ACTIVE.name(), LocalDateTime.now());

            if (activeUsers.size() < 10) {
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
    }

    @Transactional
    public void expireUserQueues() {
        List<UserQueueEntity> expiredQueues = userQueueRepository.getTop10UserStatusByExpireAt(QueueStatus.ACTIVE.name(), LocalDateTime.now());

        for (UserQueueEntity queue : expiredQueues) {
            expireUserQueueToken(queue.getUserId());
        }
    }

    public void standbyUserQueueToken(String userId) {
        updateQueueStatus(userId, QueueStatus.STANDBY);
    }

    public void expireUserQueueToken(String userId) {
        updateQueueStatus(userId, QueueStatus.EXPIRE);
    }

    public UserQueue getUserQueueInfo(String userId) {
        UserQueueEntity userQueue = userQueueRepository.getQueueInfo(userId)
                .orElseThrow(() -> new IllegalArgumentException("관련한 정보가 없습니다."));
        return convertToDomain(userQueue);
    }

    public void updateQueueStatus(String userId, QueueStatus queueStatus) {
        UserQueue userQueue = getUserQueueInfo(userId);

        UserQueueEntity updatedQueueEntity = UserQueueEntity.builder()
                .queueId(userQueue.getQueueId())
                .queueStatus(queueStatus.name())
                .userId(userQueue.getUserId())
                .build();
        userQueueRepository.addUserQueue(updatedQueueEntity);
    }

    public void addUserToQueue(String userId) {
        UserQueueEntity newUserQueue = UserQueueEntity.builder()
                .userId(userId)
                .queueStatus(QueueStatus.STANDBY.name())
                .build();
        userQueueRepository.addUserQueue(newUserQueue);
    }

    public boolean isUserInExpiredQueue(String userId) {
        return userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name());
    }

    public boolean isUserInQueue(String userId) {
        return userQueueRepository.checkIfUserInQueue(userId);
    }

    public boolean isUserActive(String userId) {
        UserQueue userQueue = getUserQueueInfo(userId);

        return userQueue.getQueueStatus().equals(QueueStatus.ACTIVE.name());
    }

    public int countUsersInQueue(LocalDateTime createAt) {
        return userQueueRepository.countByQueue(createAt);
    }

    public void removeUserQueueToken(String userId) {
        UserQueue userQueue = getUserQueueInfo(userId);
        userQueueRepository.removeUserQueueToken(userQueue.getQueueId());
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
