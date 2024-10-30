package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeatRepositoryImpl implements SeatRepository {

    private final JpaSeatRepository jpaSeatRepository;

    public SeatRepositoryImpl(JpaSeatRepository jpaSeatRepository) {
        this.jpaSeatRepository = jpaSeatRepository;
    }

    @Override
    public Optional<SeatEntity> getSeatInfo(Long seatId) {
        return jpaSeatRepository.findBySeatId(seatId);
    }

    @Override
    public List<SeatEntity> getSeatNumberAndSeatStatus(Long concertScheduleId, String seatStatus) {
        return jpaSeatRepository.findByConcertScheduleIdAndSeatStatus(concertScheduleId, seatStatus);
    }

    @Override
    public Optional<SeatEntity> getSeatNumberAndSeatNumber(Long concertScheduleId, Long seatNumber) {
        return jpaSeatRepository.findByConcertScheduleIdAndSeatNumber(concertScheduleId, seatNumber);
    }

    @Override
    public Optional<SeatEntity> checkSeatNumberStatus(Long seatNumber) {
        return jpaSeatRepository.findBySeatNumber(seatNumber);
    }

    @Override
    public SeatEntity save(SeatEntity seatEntity) {
        return jpaSeatRepository.save(seatEntity);
    }

}