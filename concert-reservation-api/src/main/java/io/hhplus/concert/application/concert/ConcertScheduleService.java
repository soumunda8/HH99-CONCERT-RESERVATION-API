package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConcertScheduleService {

    private final ConcertScheduleRepository concertScheduleRepository;

    public ConcertScheduleService(ConcertScheduleRepository concertScheduleRepository) {
        this.concertScheduleRepository = concertScheduleRepository;
    }

    public ConcertSchedule getConcertScheduleInfo(Long concertScheduleId) {
        ConcertScheduleEntity concertScheduleEntity = concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트 일정이 존재하지 않습니다."));

        return convertToDomain(concertScheduleEntity);
    }

    public void getReservationDate(ConcertSchedule concertSchedule) {
        LocalDateTime today = LocalDateTime.now();
        if (!concertSchedule.getAvailableReservationDate().isAfter(today)) {
            throw new IllegalArgumentException("해당 콘서트 예약 가능 날짜가 아닙니다.");
        }
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