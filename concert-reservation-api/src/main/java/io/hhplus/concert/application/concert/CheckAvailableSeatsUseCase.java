package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckAvailableSeatsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CheckAvailableSeatsUseCase.class);

    private final SeatService seatService;
    private final ConcertScheduleService concertScheduleService;

    public CheckAvailableSeatsUseCase(SeatService seatService, ConcertScheduleService concertScheduleService) {
        this.seatService = seatService;
        this.concertScheduleService = concertScheduleService;
    }

    @Transactional(readOnly = true)
    public AvailableSeatsResponse execute(Long concertScheduleId) {
        logger.info("Checking available seats for concert schedule ID: {}", concertScheduleId);

        ConcertSchedule concertSchedule = concertScheduleService.getConcertScheduleInfo(concertScheduleId);
        logger.debug("Concert schedule info: {}", concertSchedule);

        concertScheduleService.getReservationDate(concertSchedule);
        logger.debug("Checked reservation date for concert schedule ID: {}", concertScheduleId);

        Long remainingSeats = seatService.calculateRemainingSeats(concertScheduleId, concertSchedule.getMaxSeatCount());
        logger.debug("Calculated remaining seats: {}", remainingSeats);

        List<Long> reservedSeatNumbers = seatService.getReservedSeatNumbers(concertScheduleId);
        logger.debug("Reserved seat numbers for concert schedule ID {}: {}", concertScheduleId, reservedSeatNumbers);

        AvailableSeatsResponse availableSeatsResponse = new AvailableSeatsResponse();
        availableSeatsResponse.setRemainingSeats(remainingSeats);
        availableSeatsResponse.setConcertScheduleId(concertScheduleId);
        availableSeatsResponse.setReservedSeatNumbers(reservedSeatNumbers);

        logger.info("Available seats response generated successfully for concert schedule ID: {}", concertScheduleId);

        return availableSeatsResponse;
    }

}