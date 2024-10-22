package io.hhplus.concert.domain.repository.user;

import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface UserQueueRepository {

    boolean checkIfUserInQueueWithStatus(String userId, String queueStatus);
    boolean checkIfUserInQueue(String userId);
    UserQueueEntity getQueueInfo(String userId);
    void save(UserQueueEntity userQueue);
    int countByQueue(LocalDateTime createAt);
    List<UserQueueEntity> getAllStandByList(String queueStatus);
    List<UserQueueEntity> getAllActiveByList(LocalDateTime createAt, Pageable pageable);

}