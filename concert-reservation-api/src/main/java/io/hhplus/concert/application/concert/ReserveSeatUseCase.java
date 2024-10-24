package io.hhplus.concert.application.concert;

import io.hhplus.concert.application.reservation.ReservationService;
import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ReserveSeatUseCase {

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
        checkUserStatusUseCase.execute(userId);

        AvailableSeatsResponse availableSeatsResponse = checkAvailableSeatsUseCase.execute(concertScheduleId);
        if (availableSeatsResponse == null || availableSeatsResponse.getReservedSeatNumbers().isEmpty()) {
            throw new IllegalArgumentException("예약 불가 날짜");
        }

        Seat seatInfo = seatService.reserveSeat(seatNumber, concertScheduleId, userId);
        Reservation reservation = reservationService.addReservation(seatInfo.getSeatId(), userId);

        return reservation;
    }

}
