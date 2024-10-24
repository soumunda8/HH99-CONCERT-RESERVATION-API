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
@Table(name = "reservation")
public class ReservationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private String reservationStatus;

    @Column(name = "reservation_expire_at")
    private LocalDateTime reservationExpireAt;

}