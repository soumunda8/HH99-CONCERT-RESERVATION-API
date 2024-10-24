package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckAvailableSeatsUseCase {

    private final SeatService seatService;
    private final ConcertScheduleService concertScheduleService;

    public CheckAvailableSeatsUseCase(SeatService seatService, ConcertScheduleService concertScheduleService) {
        this.seatService = seatService;
        this.concertScheduleService = concertScheduleService;
    }

    @Transactional(readOnly = true)
    public AvailableSeatsResponse execute(Long concertScheduleId) {
        ConcertSchedule concertSchedule = concertScheduleService.getConcertScheduleInfo(concertScheduleId);
        concertScheduleService.getReservationDate(concertSchedule);

        Long remainingSeats = seatService.calculateRemainingSeats(concertScheduleId, concertSchedule.getMaxSeatCount());
        List<Long> reservedSeatNumbers = seatService.getReservedSeatNumbers(concertScheduleId);

        AvailableSeatsResponse availableSeatsResponse = new AvailableSeatsResponse();
        availableSeatsResponse.setRemainingSeats(remainingSeats);
        availableSeatsResponse.setConcertScheduleId(concertScheduleId);
        availableSeatsResponse.setReservedSeatNumbers(reservedSeatNumbers);

        return availableSeatsResponse;
    }

}
