package io.hhplus.concert.infrastructure.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface JpaUserQueueRepository extends JpaRepository<UserQueueEntity, Long> {

    boolean existsByUserId(String userId);

    boolean existsByUserIdAndQueueStatusNot(String userId, String queueStatus);

    UserQueueEntity findByUserId(String userId);

    @Query("SELECT COUNT(u) FROM UserQueueEntity u WHERE u.queueStatus IN ('STANDBY', 'ACTIVE') AND u.createAt < :createAt")
    int countByQueue(@Param("createAt") LocalDateTime createAt);

}