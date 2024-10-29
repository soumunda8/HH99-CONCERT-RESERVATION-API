package io.hhplus.concert.infrastructure.mapper.concert;

import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;

public class SeatMapper {

    public static Seat toDomain(SeatEntity entity) {
        return new Seat(
                entity.getSeatId(),
                entity.getConcertScheduleId(),
                entity.getSeatNumber(),
                SeatStatus.valueOf(entity.getSeatStatus()),
                entity.getUserId(),
                entity.getCreateAt(),
                entity.getSeatExpireAt()
        );
    }

    public static SeatEntity toEntity(Seat seat) {
        return SeatEntity.builder()
                .seatId(seat.getSeatId())
                .concertScheduleId(seat.getConcertScheduleId())
                .seatNumber(seat.getSeatNumber())
                .seatStatus(seat.getSeatStatus().name())
                .userId(seat.getUserId())
                .seatExpireAt(seat.getExpireAt())
                .build();
    }

}