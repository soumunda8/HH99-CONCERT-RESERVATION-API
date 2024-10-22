package io.hhplus.concert.domain.repository.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;

public interface ConcertRepository {

    ConcertEntity save(ConcertEntity concert);
    long count();

}