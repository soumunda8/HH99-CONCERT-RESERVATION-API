package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertScheduleRepository extends JpaRepository<ConcertScheduleEntity, Long> {
}