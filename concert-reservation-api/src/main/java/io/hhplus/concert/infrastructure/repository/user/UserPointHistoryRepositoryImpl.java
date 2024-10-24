package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.user.UserPointHistoryRepository;
import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {

    private final JpaUserPointHistoryRepository jpaUserPointHistoryRepository;

    public UserPointHistoryRepositoryImpl(JpaUserPointHistoryRepository jpaUserPointHistoryRepository) {
        this.jpaUserPointHistoryRepository = jpaUserPointHistoryRepository;
    }

    @Override
    public void addInfo(UserPointHistoryEntity userPointHistoryEntity) {
        jpaUserPointHistoryRepository.save(userPointHistoryEntity);
    }
}