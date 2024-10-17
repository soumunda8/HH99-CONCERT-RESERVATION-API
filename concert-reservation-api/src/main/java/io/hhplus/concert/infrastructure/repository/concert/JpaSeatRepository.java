package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSeatRepository extends JpaRepository<SeatEntity, Long> {


    List<SeatEntity> findByConcertScheduleIdAndSeatStatus(Long concertScheduleId, String seatStatus);

}