package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
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

        Seat seat = convertToDomain(seatEntity);
        logger.debug("Seat info retrieved: {}", seat);

        return seat;
    }

    public Long calculateRemainingSeats(Long concertScheduleId, Long maxSeatCount) {
        logger.info("Calculating remaining seats for concertScheduleId: {}", concertScheduleId);

        List<SeatEntity> reservedSeats = seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name());

        Long reservedSeatCount = (long) reservedSeats.size();
        Long remainingSeats = maxSeatCount - reservedSeatCount;

        logger.debug("Reserved seat count: {}, remaining seats: {}", reservedSeatCount, remainingSeats);

        if (remainingSeats <= 0) {
            logger.warn("No remaining seats for concertScheduleId: {}", concertScheduleId);
            throw new IllegalArgumentException("해당 콘서트의 잔여 좌석이 없습니다.");
        }

        return remainingSeats;
    }

    public List<Long> getReservedSeatNumbers(Long concertScheduleId) {
        logger.info("Fetching reserved seat numbers for concertScheduleId: {}", concertScheduleId);

        List<Long> reservedSeatNumbers = seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name())
                .stream()
                .map(SeatEntity::getSeatNumber)
                .collect(Collectors.toList());

        logger.debug("Reserved seat numbers: {}", reservedSeatNumbers);
        return reservedSeatNumbers;
    }

    public Seat findSeatByNumber(Long seatNumber) {
        logger.info("Finding seat by seat number: {}", seatNumber);

        SeatEntity seatEntity = seatRepository.checkSeatNumberStatus(seatNumber)
                .orElseThrow(() -> {
                    logger.error("Seat with number {} not found", seatNumber);
                    return new IllegalArgumentException("좌석을 찾을 수 없습니다.");
                });

        Seat seat = convertToDomain(seatEntity);
        logger.debug("Seat info retrieved: {}", seat);

        return seat;
    }

    public Seat reserveSeat(Long seatNumber, Long concertScheduleId, String userId) {
        logger.info("Reserving seat: seatNumber: {}, concertScheduleId: {}, userId: {}", seatNumber, concertScheduleId, userId);

        Seat seat = findSeatByNumber(seatNumber);

        if (seat.getSeatStatus().equals(SeatStatus.DONE.name())) {
            logger.warn("Seat with number {} is already reserved", seatNumber);
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
        Seat newSeat = convertToDomain(newSeatEntity);

        logger.info("Seat reserved successfully: seatNumber: {}, concertScheduleId: {}, userId: {}", seatNumber, concertScheduleId, userId);

        return newSeat;
    }

    private Seat convertToDomain(SeatEntity seatEntity) {
        Seat seat = new Seat();
        seat.setSeatStatus(SeatStatus.valueOf(seatEntity.getSeatStatus()));
        seat.setSeatId(seatEntity.getSeatId());
        seat.setSeatNumber(seatEntity.getSeatNumber());
        seat.setExpireAt(seatEntity.getSeatExpireAt());
        seat.setCreateAt(seatEntity.getCreateAt());
        seat.setUserId(seatEntity.getUserId());
        seat.setConcertScheduleId(seatEntity.getConcertScheduleId());
        return seat;
    }

}