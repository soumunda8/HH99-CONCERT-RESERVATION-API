package io.hhplus.concert.infrastructure.entity.reservation;

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
@Table(name = "outbox_kafka")
public class OutBoxKafkaEntity extends BaseEntity {

    /*1) id
2) key
3) payload
4) status
5) topic
6) create_at
7) update_at
8) type ~~~*/

    @Id
    private String userId;

    @Column(nullable = false)
    private String outboxKey;

    @Column(nullable = false)
    private String outboxPayload;

    @Column(nullable = false)
    private String outboxStatus;

    @Column(nullable = false)
    private String outboxTopic;

    @Column(name = "outbox_expire_at")
    private LocalDateTime outboxExpireAt;

}