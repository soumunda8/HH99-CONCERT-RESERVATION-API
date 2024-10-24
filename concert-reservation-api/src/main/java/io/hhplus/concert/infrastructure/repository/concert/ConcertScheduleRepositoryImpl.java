package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    @Override
    public Optional<ConcertScheduleEntity> getConcertScheduleInfo(Long concertScheduleId) {
        return jpaConcertScheduleRepository.findByConcertScheduleId(concertScheduleId);
    }
}