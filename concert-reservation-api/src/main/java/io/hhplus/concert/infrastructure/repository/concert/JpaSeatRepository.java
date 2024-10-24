package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaSeatRepository extends JpaRepository<SeatEntity, Long> {

    Optional<SeatEntity> findBySeatId(Long seatId);

    List<SeatEntity> findByConcertScheduleIdAndSeatStatus(Long concertScheduleId, String seatStatus);

    SeatEntity findByConcertScheduleIdAndSeatNumber(Long concertScheduleId, Long seatNumber);

    Optional<SeatEntity> findBySeatNumber(Long seatNumber);

}