package io.hhplus.concert.domain.reservation;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    private Long reservationId;

    private String userId;

    private Long seatId;

    private ReservationStatus reservationStatus;

    private LocalDateTime createAt;

    private LocalDateTime expireAt;

}