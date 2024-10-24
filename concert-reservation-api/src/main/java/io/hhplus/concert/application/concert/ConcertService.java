package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
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
        logger.info("Fetching concert info for concert ID: {}", concertId);

        ConcertEntity concertEntity = concertRepository.getConcertInfo(concertId)
                .orElseThrow(() -> {
                    logger.error("Concert with ID {} not found", concertId);
                    return new IllegalArgumentException("해당 콘서트가 존재하지 않습니다.");
                });

        Concert concert = convertToDomain(concertEntity);
        logger.debug("Concert info retrieved: {}", concert);

        return concert;
    }

    private Concert convertToDomain(ConcertEntity concertEntity) {
        Concert concert = new Concert();
        concert.setConcertId(concertEntity.getConcertId());
        concert.setConcertTitle(concertEntity.getConcertTitle());
        concert.setConcertPrice(concertEntity.getConcertPrice());
        return concert;
    }

}