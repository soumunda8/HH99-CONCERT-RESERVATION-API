package io.hhplus.concert.application.concert;

import io.hhplus.concert.application.reservation.ReservationService;
import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveSeatUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReserveSeatUseCase.class);

    private final SeatService seatService;
    private final ReservationService reservationService;
    private final CheckUserStatusUseCase checkUserStatusUseCase;
    private final CheckAvailableSeatsUseCase checkAvailableSeatsUseCase;

    public ReserveSeatUseCase(SeatService seatService, ReservationService reservationService, CheckUserStatusUseCase checkUserStatusUseCase, CheckAvailableSeatsUseCase checkAvailableSeatsUseCase) {
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.checkUserStatusUseCase = checkUserStatusUseCase;
        this.checkAvailableSeatsUseCase = checkAvailableSeatsUseCase;
    }

    @Transactional
    public Reservation execute(Long seatNumber, Long concertScheduleId, String userId) {
        try {
            logger.info("Starting seat reservation process for userId: {}, seatNumber: {}, concertScheduleId: {}", userId, seatNumber, concertScheduleId);

            checkUserStatusUseCase.execute(userId);
            AvailableSeatsResponse availableSeatsResponse = checkAvailableSeatsUseCase.execute(concertScheduleId);

            if (availableSeatsResponse == null || availableSeatsResponse.getReservedSeatNumbers().isEmpty()) {
                logger.warn("No available seats or reservation date invalid for concertScheduleId: {}", concertScheduleId);
                throw new IllegalArgumentException("이미 예약된 좌석입니다.");
            }

            Seat seatInfo = seatService.reserveSeat(seatNumber, concertScheduleId, userId);
            Reservation reservation = reservationService.addReservation(seatInfo.getSeatId(), userId);

            logger.info("Reservation successfully added for userId: {}, seatId: {}, reservationId: {}", userId, seatInfo.getSeatId(), reservation.getReservationId());
            return reservation;

        } catch (Exception ex) {
            logger.error("Error during seat reservation process for userId: {}, seatNumber: {}, concertScheduleId: {}", userId, seatNumber, concertScheduleId, ex);
            throw ex;
        }
    }

}
