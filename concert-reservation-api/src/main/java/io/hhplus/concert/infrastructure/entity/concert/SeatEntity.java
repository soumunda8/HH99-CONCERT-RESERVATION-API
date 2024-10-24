package io.hhplus.concert.infrastructure.entity.concert;

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
@Table(name = "seat")
public class SeatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(nullable = false)
    private Long concertScheduleId;

    @Column(nullable = false)
    private Long seatNumber;

    @Column(nullable = false)
    private String seatStatus;

    @Column(nullable = false)
    private String userId;

    @Column(name = "seat_expire_at")
    private LocalDateTime seatExpireAt;

}