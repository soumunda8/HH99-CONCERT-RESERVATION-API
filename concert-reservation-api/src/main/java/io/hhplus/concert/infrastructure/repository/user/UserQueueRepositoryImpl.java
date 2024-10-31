package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.user.UserQueueRepository;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserQueueRepositoryImpl implements UserQueueRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JpaUserQueueRepository jpaUserQueueRepository;

    public UserQueueRepositoryImpl(JpaUserQueueRepository jpaUserQueueRepository) {
        this.jpaUserQueueRepository = jpaUserQueueRepository;
    }

    @Override
    public boolean checkIfUserInQueueWithStatus(String userId, String queueStatus) {
        return jpaUserQueueRepository.existsByUserIdAndQueueStatusNot(userId, queueStatus);
    }

    @Override
    public void removeUserQueueToken(long queueId) {
        jpaUserQueueRepository.deleteById(queueId);
    }

    @Override
    public boolean checkIfUserInQueue(String userId) {
        return jpaUserQueueRepository.existsByUserId(userId);
    }

    @Override
    public Optional<UserQueueEntity> getQueueInfo(String userId) {
        return jpaUserQueueRepository.findTop1ByUserIdOrderByQueueExpireAtDesc(userId);
    }

    @Override
    public Optional<UserQueueEntity> getQueueInfoById(Long queueId) {
        return jpaUserQueueRepository.findById(queueId);
    }

    @Override
    public void addUserQueue(UserQueueEntity userQueue) {
        jpaUserQueueRepository.save(userQueue);
    }

    @Override
    public int countByQueue(LocalDateTime createAt) {
        List<String> statuses = Arrays.asList("STANDBY", "ACTIVE");
        return jpaUserQueueRepository.countByQueueStatusInAndCreateAtBefore(statuses, createAt);
    }

    @Override
    public List<UserQueueEntity> getAllByUserStatusByCreateAt(String queueStatus, LocalDateTime createAt) {
        return jpaUserQueueRepository.findByQueueStatusAndCreateAtBeforeOrderByCreateAtAsc(queueStatus, createAt);
    }

    @Override
    public List<UserQueueEntity> getTop10UserStatusByExpireAt(String queueStatus, LocalDateTime expireAt) {
        return jpaUserQueueRepository.findTop10ByQueueStatusAndQueueExpireAtBeforeOrderByQueueExpireAtAsc(queueStatus, expireAt);
    }

    @Override
    public void removeAllData() {
        jpaUserQueueRepository.deleteAll();
    }

}