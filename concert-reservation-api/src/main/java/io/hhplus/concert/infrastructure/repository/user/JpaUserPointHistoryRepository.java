package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserPointHistoryRepository extends JpaRepository<UserPointHistoryEntity, Long> {
}