package io.hhplus.concert.domain.concert;

import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {

    Optional<SeatEntity> getSeatInfo(Long seatId);
    List<SeatEntity> getSeatNumberAndSeatStatus(Long concertScheduleId, String seatStatus);
    SeatEntity getSeatNumberAndSeatNumber(Long concertScheduleId, Long seatNumber);
    Optional<SeatEntity> checkSeatNumberStatus(Long seatNumber);
    SeatEntity save(SeatEntity seatEntity);

}