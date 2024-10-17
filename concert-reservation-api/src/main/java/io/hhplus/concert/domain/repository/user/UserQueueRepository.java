package io.hhplus.concert.domain.repository.user;

import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;

import java.time.LocalDateTime;

public interface UserQueueRepository {

    boolean checkIfUserInQueueWithStatus(String userId, String queueStatus);
    boolean checkIfUserInQueue(String userId);
    UserQueueEntity getQueueInfo(String userId);
    void save(UserQueueEntity userQueue);
    int countByQueue(LocalDateTime createAt);

}