package io.hhplus.concert.domain.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;

public interface ConcertScheduleRepository {

    void save(ConcertScheduleEntity concertSchedule);

}