package io.hhplus.concert.infrastructure.entity.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_queue")
public class UserQueueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queueId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String queueStatus;

    @Column(name = "queue_expire_at")
    private LocalDateTime queueExpireAt;

}