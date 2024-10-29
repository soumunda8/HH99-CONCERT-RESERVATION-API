package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.mapper.concert.ConcertScheduleMapper;
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
        logger.info("Retrieving concert schedule info for ID: {}", concertScheduleId);

        return concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)
                .map(ConcertScheduleMapper::toDomain)
                .orElseThrow(() -> {
                    logger.error("Concert schedule with ID {} not found", concertScheduleId);
                    return new IllegalArgumentException("해당 콘서트 일정이 존재하지 않습니다.");
                });
    }

    public void checkReservationDate(ConcertSchedule concertSchedule) {
        LocalDateTime today = LocalDateTime.now();
        if (!concertSchedule.getAvailableReservationDate().isAfter(today)) {
            logger.warn("Reservation date invalid for concert schedule ID: {} - Available date: {}, Current date: {}",
                    concertSchedule.getConcertScheduleId(),
                    concertSchedule.getAvailableReservationDate(),
                    today);
            throw new IllegalArgumentException("해당 콘서트 예약 가능 날짜가 아닙니다.");
        }
        logger.info("Concert schedule ID: {} is valid for reservation", concertSchedule.getConcertScheduleId());
    }

}