package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.repository.user.UserPointHistoryRepository;
import io.hhplus.concert.domain.repository.user.UserQueueRepository;
import io.hhplus.concert.domain.repository.user.UserRepository;
import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueueRepository userQueueRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    public UserService(UserRepository userRepository, UserQueueRepository userQueueRepository, UserPointHistoryRepository userPointHistoryRepository) {

        this.userRepository =userRepository;
        this.userQueueRepository = userQueueRepository;
        this.userPointHistoryRepository = userPointHistoryRepository;
    }

    public void addQueue(String userId) {
        // 대기열 테이블 내 EXPIRE 상태로 있는지 사용자 존재 확인
        boolean isUserInExpired = userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name());
        if (isUserInExpired) {
            updateQueueStatusToStandby(userId);
        }

        // 대기열 테이블 내 데이터 없으면 추가
        boolean isUserInQueue = userQueueRepository.checkIfUserInQueue(userId);
        if (isUserInQueue) {
            addUserToQueue(userId);
        }
    }

    private void updateQueueStatusToStandby(String userId) {
        // EXPIRE 상태 => STANDBY 상태 변경
        UserQueueEntity userQueueEntity = userQueueRepository.getQueueInfo(userId);

        if (userQueueEntity == null) {
            throw new IllegalArgumentException("관련한 정보가 없습니다.");
        }
        UserQueueEntity updatedQueueEntity = UserQueueEntity.builder()
                .queueId(userQueueEntity.getQueueId())
                .queueStatus(QueueStatus.STANDBY.name())
                .userId(userQueueEntity.getUserId())
                .build();
        userQueueRepository.save(updatedQueueEntity);
    }

    private void addUserToQueue(String userId) {
        // 대기열 테이블 내 신규 사용자 추가
        UserQueueEntity newUserQueue = UserQueueEntity.builder()
                .userId(userId)
                .queueStatus(QueueStatus.STANDBY.name())
                .build();
        userQueueRepository.save(newUserQueue);
    }

    @Transactional(readOnly = true)
    public int countQueues(String userId) {
        UserQueueEntity userQueueEntity = userQueueRepository.getQueueInfo(userId);

        if (userQueueEntity == null) {
            throw new IllegalArgumentException("관련한 정보가 없습니다.");
        }

        LocalDateTime createAt = userQueueEntity.getCreateAt();

        return userQueueRepository.countByQueue(createAt);
    }

    public void checkUserStatus(String userId) {
        UserQueueEntity userQueue = userQueueRepository.getQueueInfo(userId);

        if (userQueue == null) {
            throw new IllegalArgumentException("관련한 정보가 없습니다.");
        }

        if (!userQueue.getQueueStatus().equals(QueueStatus.ACTIVE.name())) {
            throw new IllegalArgumentException("사용자가 활성상태가 아닙니다.");
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndActivateQueue() {
        List<UserQueueEntity> allStandbyUsers = userQueueRepository.getAllStandByList(QueueStatus.STANDBY.name());

        for (UserQueueEntity user : allStandbyUsers) {
            List<UserQueueEntity> activeUsers = userQueueRepository.getAllActiveByList(user.getCreateAt(), (Pageable) PageRequest.of(0, 10));

            if (activeUsers.size() < 10) {
                UserQueueEntity tenthUser = activeUsers.size() == 9 ? user : activeUsers.get(9);
                LocalDateTime expireAt = LocalDateTime.now().plusMinutes(10);
                UserQueueEntity uploadQueue = UserQueueEntity.builder()
                        .queueId(tenthUser.getQueueId())
                        .userId(tenthUser.getUserId())
                        .queueStatus(QueueStatus.ACTIVE.name())
                        .queueExpireAt(expireAt)
                        .build();
                userQueueRepository.save(uploadQueue);
            }
        }
    }

}