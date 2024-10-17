package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.repository.concert.SeatRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SeatRepositoryImpl implements SeatRepository {

    private final JpaSeatRepository jpaSeatRepository;

    public SeatRepositoryImpl(JpaSeatRepository jpaSeatRepository) {
        this.jpaSeatRepository = jpaSeatRepository;
    }

}