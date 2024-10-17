package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserQueueRepository extends JpaRepository<UserQueueEntity, Long> {
}