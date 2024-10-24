package io.hhplus.concert.domain.user;

import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserQueueRepository {

    boolean checkIfUserInQueueWithStatus(String userId, String queueStatus);
    boolean checkIfUserInQueue(String userId);
    Optional<UserQueueEntity> getQueueInfo(String userId);
    void addUserQueue(UserQueueEntity userQueue);
    void removeUserQueueToken(long queueId);
    int countByQueue(LocalDateTime createAt);
    List<UserQueueEntity> getAllByUserStatusByCreateAt(String queueStatus, LocalDateTime createAt);
    List<UserQueueEntity> getTop10UserStatusByExpireAt(String queueStatus, LocalDateTime expireAt);

}