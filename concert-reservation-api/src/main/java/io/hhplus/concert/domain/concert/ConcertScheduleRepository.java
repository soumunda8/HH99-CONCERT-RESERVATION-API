package io.hhplus.concert.domain.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;

import java.util.Optional;

public interface ConcertScheduleRepository {

    void save(ConcertScheduleEntity concertSchedule);

    Optional<ConcertScheduleEntity> getConcertScheduleInfo(Long concertScheduleId);

}