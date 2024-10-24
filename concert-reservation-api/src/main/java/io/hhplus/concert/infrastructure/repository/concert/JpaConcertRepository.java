package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaConcertRepository extends JpaRepository<ConcertEntity, Long> {

    Optional<ConcertEntity> findByConcertId(Long concertId);

}