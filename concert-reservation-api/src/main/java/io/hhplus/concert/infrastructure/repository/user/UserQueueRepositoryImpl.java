package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.repository.user.UserQueueRepository;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class UserQueueRepositoryImpl implements UserQueueRepository {

    private final JpaUserQueueRepository jpaUserQueueRepository;

    public UserQueueRepositoryImpl(JpaUserQueueRepository jpaUserQueueRepository) {
        this.jpaUserQueueRepository = jpaUserQueueRepository;
    }

    @Override
    public boolean checkIfUserInQueueWithStatus(String userId, String queueStatus) {
        return jpaUserQueueRepository.existsByUserIdAndQueueStatusNot(userId, queueStatus);
    }

    @Override
    public boolean checkIfUserInQueue(String userId) {
        return jpaUserQueueRepository.existsByUserId(userId);
    }

    @Override
    public UserQueueEntity getQueueInfo(String userId) {
        return jpaUserQueueRepository.findByUserId(userId);
    }

    @Override
    public void save(UserQueueEntity userQueue) {
        jpaUserQueueRepository.save(userQueue);
    }

    @Override
    public int countByQueue(LocalDateTime createAt) {
        return jpaUserQueueRepository.countByQueue(createAt);
    }
}