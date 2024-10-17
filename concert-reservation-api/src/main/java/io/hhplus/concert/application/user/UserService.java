package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.repository.user.UserPointHistoryRepository;
import io.hhplus.concert.domain.repository.user.UserQueueRepository;
import io.hhplus.concert.domain.repository.user.UserRepository;
import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserQueueRepository userQueueRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    public UserService(ModelMapper modelMapper, UserRepository userRepository, UserQueueRepository userQueueRepository, UserPointHistoryRepository userPointHistoryRepository) {
        this.modelMapper = modelMapper;
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
        UserQueueEntity userQueue = userQueueRepository.getQueueInfo(userId);

        if (userQueue == null) {
            throw new IllegalArgumentException("관련한 정보가 없습니다.");
        }

        return userQueueRepository.countByQueue(userQueue.getCreateAt());
    }

}