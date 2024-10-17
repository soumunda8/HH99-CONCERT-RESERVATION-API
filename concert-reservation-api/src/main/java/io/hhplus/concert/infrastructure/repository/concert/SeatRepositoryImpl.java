package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.repository.concert.SeatRepository;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatRepositoryImpl implements SeatRepository {

    private final JpaSeatRepository jpaSeatRepository;

    public SeatRepositoryImpl(JpaSeatRepository jpaSeatRepository) {
        this.jpaSeatRepository = jpaSeatRepository;
    }

    @Override
    public List<SeatEntity> getSeatStatusDONE(Long concertScheduleId, String seatStatus) {
        return jpaSeatRepository.findByConcertScheduleIdAndSeatStatus(concertScheduleId, seatStatus);
    }
}