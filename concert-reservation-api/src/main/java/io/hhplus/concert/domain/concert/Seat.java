package io.hhplus.concert.domain.concert;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    private Long seatId;

    private Long concertScheduleId;

    private Long seatNumber;

    private SeatStatus seatStatus;

    private String userId;

    private LocalDateTime createAt;

    private LocalDateTime expireAt;

}