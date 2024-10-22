package io.hhplus.concert.domain.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {

    List<SeatEntity> getSeatStatusDONE(Long concertScheduleId, String seatStatus);
    Optional<SeatEntity> chekcSeatNumberStatus(Long seatNumber);
    SeatEntity save(SeatEntity seatEntity);

}