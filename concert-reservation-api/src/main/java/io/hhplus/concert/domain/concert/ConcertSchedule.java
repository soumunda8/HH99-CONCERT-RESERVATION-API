package io.hhplus.concert.domain.concert;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConcertSchedule {

    private Long concertScheduleId;

    private Long concertId;

    private LocalDateTime availableReservationDate;

    private Long maxSeatCount;

    private Long remainingSeatCount = 0L;

}