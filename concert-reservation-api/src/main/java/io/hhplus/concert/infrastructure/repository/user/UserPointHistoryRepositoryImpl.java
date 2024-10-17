package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.repository.user.UserPointHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {

    private final JpaUserPointHistoryRepository jpaUserPointHistoryRepository;

    public UserPointHistoryRepositoryImpl(JpaUserPointHistoryRepository jpaUserPointHistoryRepository) {
        this.jpaUserPointHistoryRepository = jpaUserPointHistoryRepository;
    }

}