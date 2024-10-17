package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.repository.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {

    private final JpaConcertScheduleRepository jpaConcertScheduleRepository;

    public ConcertScheduleRepositoryImpl(JpaConcertScheduleRepository jpaConcertScheduleRepository) {
        this.jpaConcertScheduleRepository = jpaConcertScheduleRepository;
    }

    @Override
    public void save(ConcertScheduleEntity concertSchedule) {
        jpaConcertScheduleRepository.save(concertSchedule);
    }
}