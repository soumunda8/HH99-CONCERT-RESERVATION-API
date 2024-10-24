package io.hhplus.concert.domain.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;

import java.util.Optional;

public interface ConcertRepository {

    Optional<ConcertEntity> getConcertInfo(Long concertId);
    ConcertEntity save(ConcertEntity concert);
    long count();

}