package io.hhplus.concert.domain.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;

import java.util.List;

public interface SeatRepository {

    List<SeatEntity> getSeatStatusDONE(Long concertScheduleId, String seatStatus);

}