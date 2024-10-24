package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public Seat getSeatInfo(long seatId) {
        SeatEntity seatEntity = seatRepository.getSeatInfo(seatId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        return convertToDomain(seatEntity);
    }

    public Long calculateRemainingSeats(Long concertScheduleId, Long maxSeatCount) {
        List<SeatEntity> reservedSeats = seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name());

        Long reservedSeatCount = (long) reservedSeats.size();
        Long remainingSeats = maxSeatCount - reservedSeatCount;

        if (remainingSeats <= 0) {
            throw new IllegalArgumentException("해당 콘서트의 잔여 좌석이 없습니다.");
        }

        return remainingSeats;
    }

    public List<Long> getReservedSeatNumbers(Long concertScheduleId) {
        return seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name())
                .stream()
                .map(SeatEntity::getSeatNumber)
                .collect(Collectors.toList());
    }

    public Seat findSeatByNumber(Long seatNumber) {
        SeatEntity seatEntity = seatRepository.checkSeatNumberStatus(seatNumber)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        return convertToDomain(seatEntity);
    }

    public Seat reserveSeat(Long seatNumber, Long concertScheduleId, String userId) {
        Seat seat = findSeatByNumber(seatNumber);

        if (seat.getSeatStatus().equals(SeatStatus.DONE.name())) {
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
        return convertToDomain(newSeatEntity);
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