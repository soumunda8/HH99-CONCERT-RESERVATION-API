package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.repository.concert.ConcertRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;

    public ConcertRepositoryImpl(JpaConcertRepository jpaConcertRepository) {
        this.jpaConcertRepository = jpaConcertRepository;
    }

    @Override
    public ConcertEntity save(ConcertEntity concert) {
        return jpaConcertRepository.save(concert);
    }

    @Override
    public long count() {
        return jpaConcertRepository.count();
    }
}