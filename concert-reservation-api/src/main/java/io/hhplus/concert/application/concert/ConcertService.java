package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.infrastructure.mapper.concert.ConcertMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConcertService {

    private static final Logger logger = LoggerFactory.getLogger(ConcertService.class);
    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public Concert getConcertInfo(long concertId) {
        logger.info("Retrieving concert info for ID: {}", concertId);

        return concertRepository.getConcertInfo(concertId)
                .map(ConcertMapper::toDomain)
                .orElseThrow(() -> {
                    logger.error("Concert with ID {} not found", concertId);
                    return new IllegalArgumentException("해당 콘서트가 존재하지 않습니다.");
                });
    }

}