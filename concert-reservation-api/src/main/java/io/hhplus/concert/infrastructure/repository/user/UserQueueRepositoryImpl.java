package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.repository.user.UserQueueRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserQueueRepositoryImpl implements UserQueueRepository {

    private final JpaUserQueueRepository jpaUserQueueRepository;

    public UserQueueRepositoryImpl(JpaUserQueueRepository jpaUserQueueRepository) {
        this.jpaUserQueueRepository = jpaUserQueueRepository;
    }

}