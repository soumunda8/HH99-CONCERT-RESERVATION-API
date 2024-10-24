package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import org.springframework.stereotype.Service;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public Concert getConcertInfo(long concertId) {
        ConcertEntity concert = concertRepository.getConcertInfo(concertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트가 존재하지 않습니다."));
        return convertToDomain(concert);
    }

    private Concert convertToDomain(ConcertEntity concertEntity) {
        Concert concert = new Concert();
        concert.setConcertId(concertEntity.getConcertId());
        concert.setConcertTitle(concertEntity.getConcertTitle());
        concert.setConcertPrice(concertEntity.getConcertPrice());
        return concert;
    }

}