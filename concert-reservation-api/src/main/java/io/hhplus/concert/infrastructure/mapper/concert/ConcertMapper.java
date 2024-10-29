package io.hhplus.concert.infrastructure.mapper.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;

public class ConcertMapper {

    public static Concert toDomain(ConcertEntity entity) {
        return new Concert(
                entity.getConcertId(),
                entity.getConcertTitle(),
                entity.getConcertPrice()
        );
    }

    public static ConcertEntity toEntity(Concert concert) {
        return ConcertEntity.builder()
                .concertId(concert.getConcertId())
                .concertTitle(concert.getConcertTitle())
                .concertPrice(concert.getConcertPrice())
                .build();
    }

}