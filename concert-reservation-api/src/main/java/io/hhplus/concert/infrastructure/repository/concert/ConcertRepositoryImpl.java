package io.hhplus.concert.infrastructure.repository.concert;

import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;

    public ConcertRepositoryImpl(JpaConcertRepository jpaConcertRepository) {
        this.jpaConcertRepository = jpaConcertRepository;
    }

    @Override
    public Optional<ConcertEntity> getConcertInfo(Long concertId) {
        return jpaConcertRepository.findByConcertId(concertId);
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