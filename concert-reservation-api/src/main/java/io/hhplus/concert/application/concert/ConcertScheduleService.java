package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConcertScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ConcertScheduleService.class);

    private final ConcertScheduleRepository concertScheduleRepository;

    public ConcertScheduleService(ConcertScheduleRepository concertScheduleRepository) {
        this.concertScheduleRepository = concertScheduleRepository;
    }

    public ConcertSchedule getConcertScheduleInfo(Long concertScheduleId) {
        logger.info("Fetching concert schedule info for ID: {}", concertScheduleId);

        ConcertScheduleEntity concertScheduleEntity = concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)
                .orElseThrow(() -> {
                    logger.error("Concert schedule with ID {} not found", concertScheduleId);
                    return new IllegalArgumentException("해당 콘서트 일정이 존재하지 않습니다.");
                });

        ConcertSchedule concertSchedule = convertToDomain(concertScheduleEntity);
        logger.debug("Concert schedule info retrieved: {}", concertSchedule);

        return concertSchedule;
    }

    public void getReservationDate(ConcertSchedule concertSchedule) {
        logger.info("Checking reservation date for concert schedule ID: {}", concertSchedule.getConcertScheduleId());

        LocalDateTime today = LocalDateTime.now();
        if (!concertSchedule.getAvailableReservationDate().isAfter(today)) {
            logger.warn("Reservation date check failed for concert schedule ID: {} - Available date: {}, Current date: {}",
                    concertSchedule.getConcertScheduleId(),
                    concertSchedule.getAvailableReservationDate(),
                    today);
            throw new IllegalArgumentException("해당 콘서트 예약 가능 날짜가 아닙니다.");
        }

        logger.info("Concert schedule ID: {} is valid for reservation", concertSchedule.getConcertScheduleId());
    }


    private ConcertSchedule convertToDomain(ConcertScheduleEntity concertScheduleEntity) {
        ConcertSchedule concertSchedule = new ConcertSchedule();
        concertSchedule.setConcertScheduleId(concertScheduleEntity.getConcertScheduleId());
        concertSchedule.setConcertId(concertScheduleEntity.getConcertId());
        concertSchedule.setMaxSeatCount(concertScheduleEntity.getMaxSeatCount());
        concertSchedule.setRemainingSeatCount(concertScheduleEntity.getRemainingSeatCount());
        concertSchedule.setAvailableReservationDate(concertScheduleEntity.getAvailableReservationDate());
        return concertSchedule;
    }

}