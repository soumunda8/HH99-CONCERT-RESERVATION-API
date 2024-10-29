package io.hhplus.concert.infrastructure.mapper.concert;

import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;

public class ConcertScheduleMapper {

    public static ConcertSchedule toDomain(ConcertScheduleEntity entity) {
        return new ConcertSchedule(
                entity.getConcertScheduleId(),
                entity.getConcertId(),
                entity.getAvailableReservationDate(),
                entity.getMaxSeatCount(),
                entity.getRemainingSeatCount()
        );
    }

    public static ConcertScheduleEntity toEntity(ConcertSchedule concertSchedule) {
        return ConcertScheduleEntity.builder()
                .concertScheduleId(concertSchedule.getConcertScheduleId())
                .concertId(concertSchedule.getConcertId())
                .availableReservationDate(concertSchedule.getAvailableReservationDate())
                .maxSeatCount(concertSchedule.getMaxSeatCount())
                .remainingSeatCount(concertSchedule.getRemainingSeatCount())
                .build();
    }

}