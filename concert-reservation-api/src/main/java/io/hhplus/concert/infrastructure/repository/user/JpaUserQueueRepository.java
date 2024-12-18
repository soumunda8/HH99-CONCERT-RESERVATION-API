package io.hhplus.concert.infrastructure.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaUserQueueRepository extends JpaRepository<UserQueueEntity, Long> {

    boolean existsByUserId(String userId);

    boolean existsByUserIdAndQueueStatusNot(String userId, String queueStatus);

    Optional<UserQueueEntity> findTop1ByUserIdOrderByQueueExpireAtDesc(String userId);

    int countByQueueStatusInAndCreateAtBefore(List<String> queueStatuses, LocalDateTime createAt);

    List<UserQueueEntity> findByQueueStatusAndCreateAtBeforeOrderByCreateAtAsc(String queueStatus, LocalDateTime createAt);

    List<UserQueueEntity> findTop10ByQueueStatusAndQueueExpireAtBeforeOrderByQueueExpireAtAsc(String queueStatus, LocalDateTime expireAt);

}