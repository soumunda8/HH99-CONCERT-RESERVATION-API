package io.hhplus.concert.infrastructure.entity.concert;

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
@Table(name = "concert_schedule")
public class ConcertScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertScheduleId;

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private LocalDateTime availableReservationDate;

    @Column(nullable = false)
    private Long maxSeatCount;

    @Column(nullable = false)
    private Long remainingSeatCount;

}