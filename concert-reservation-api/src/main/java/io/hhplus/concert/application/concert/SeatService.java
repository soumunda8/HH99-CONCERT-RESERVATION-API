package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import io.hhplus.concert.infrastructure.mapper.concert.SeatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public Seat getSeatInfo(long seatId) {
        logger.info("Fetching seat info for seat ID: {}", seatId);

        SeatEntity seatEntity = seatRepository.getSeatInfo(seatId)
                .orElseThrow(() -> {
                    logger.error("Seat with ID {} not found", seatId);
                    return new IllegalArgumentException("예약 정보를 찾을 수 없습니다.");
                });

        return SeatMapper.toDomain(seatEntity);
    }

    public Long calculateRemainingSeats(Long concertScheduleId, Long maxSeatCount) {
        logger.info("Calculating remaining seats for concertScheduleId: {}", concertScheduleId);

        List<SeatEntity> reservedSeats = seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name());
        Long remainingSeats = maxSeatCount - reservedSeats.size();

        if (remainingSeats <= 0) {
            logger.warn("No remaining seats for concertScheduleId: {}", concertScheduleId);
            throw new IllegalArgumentException("해당 콘서트의 잔여 좌석이 없습니다.");
        }

        logger.info("Remaining seats calculated: {}", remainingSeats);
        return remainingSeats;
    }

    public List<Long> getReservedSeatNumbers(Long concertScheduleId) {
        logger.info("Fetching reserved seat numbers for concertScheduleId: {}", concertScheduleId);

        return seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name())
                .stream()
                .map(SeatEntity::getSeatNumber)
                .collect(Collectors.toList());
    }

    public Seat findSeatByNumber(Long seatNumber) {
        logger.info("Finding seat by seat number: {}", seatNumber);

        SeatEntity seatEntity = seatRepository.checkSeatNumberStatus(seatNumber)
                .orElseThrow(() -> {
                    logger.error("Seat with number {} not found", seatNumber);
                    return new IllegalArgumentException("좌석을 찾을 수 없습니다.");
                });

        return SeatMapper.toDomain(seatEntity);
    }

    public Seat reserveSeat(Long seatNumber, Long concertScheduleId, String userId) {
        logger.info("Reserving seat: seatNumber: {}, concertScheduleId: {}, userId: {}", seatNumber, concertScheduleId, userId);

        Seat seat = findSeatByNumber(seatNumber);

        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            logger.warn("Seat with number {} is not available for reservation", seatNumber);
            throw new IllegalArgumentException("이미 예약된 좌석입니다.");
        }

        SeatEntity reservedSeat = SeatEntity.builder()
                .concertScheduleId(concertScheduleId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.HELD.name())
                .userId(userId)
                .seatExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();

        SeatEntity newSeatEntity = seatRepository.save(reservedSeat);
        logger.info("Seat reserved successfully for seatNumber: {}, concertScheduleId: {}, userId: {}", seatNumber, concertScheduleId, userId);

        return SeatMapper.toDomain(newSeatEntity);
    }

}