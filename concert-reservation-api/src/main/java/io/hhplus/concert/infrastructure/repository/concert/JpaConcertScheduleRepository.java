package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaConcertScheduleRepository extends JpaRepository<ConcertScheduleEntity, Long> {

    Optional<ConcertScheduleEntity> findByConcertScheduleId(Long concertScheduleId);


}