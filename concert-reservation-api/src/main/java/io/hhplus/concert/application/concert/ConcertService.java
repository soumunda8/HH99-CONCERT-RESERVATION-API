package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.domain.repository.concert.ConcertRepository;
import io.hhplus.concert.domain.repository.concert.ConcertScheduleRepository;
import io.hhplus.concert.domain.repository.concert.SeatRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConcertService {

    private final ModelMapper modelMapper;
    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    public ConcertService(ModelMapper modelMapper, ConcertRepository concertRepository, ConcertScheduleRepository concertScheduleRepository, SeatRepository seatRepository) {
        this.modelMapper = modelMapper;
        this.concertRepository = concertRepository;
        this.concertScheduleRepository = concertScheduleRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAvailable(Long concertScheduleId) {
        ConcertScheduleEntity concertSchedule = concertScheduleRepository.getConcertScheduleInfo(concertScheduleId).orElseThrow(() -> new IllegalArgumentException("해당 콘서트 일정이 존재하지 않습니다."));

        LocalDateTime today = LocalDateTime.now();
        if (!concertSchedule.getAvailableReservationDate().isAfter(today)) {
            throw new IllegalArgumentException("해당 콘서트 예약 가능 날짜가 아닙니다.");
        }

        Long maxSeatCount = concertSchedule.getMaxSeatCount();
        List<SeatEntity> reservedSeatEntity = seatRepository.getSeatStatusDONE(concertScheduleId, SeatStatus.DONE.name());

        Long reservedSeatCount = (long) reservedSeatEntity.size();
        Long remainingSeats = maxSeatCount - reservedSeatCount;

        if (remainingSeats <= 0) {
            throw new IllegalArgumentException("해당 콘서트의 잔여 좌석이 없습니다.");
        }

        List<Long> reservedSeatNumbers = reservedSeatEntity.stream().map(SeatEntity::getSeatNumber).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("concertScheduleId", concertScheduleId);
        response.put("reservedSeatNumbers", reservedSeatNumbers);

        return response;
    }

}