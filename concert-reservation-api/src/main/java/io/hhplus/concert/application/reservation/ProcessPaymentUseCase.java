package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.concert.ConcertScheduleService;
import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.application.concert.SeatService;
import io.hhplus.concert.application.user.UserPointHistoryService;
import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessPaymentUseCase {

    private final ReservationService reservationService;
    private final UserService userService;
    private final UserPointHistoryService userPointHistoryService;
    private final ConcertService concertService;
    private final ConcertScheduleService concertScheduleService;
    private final SeatService seatService;

    public ProcessPaymentUseCase(ReservationService reservationService, UserService userService, UserPointHistoryService userPointHistoryService, ConcertService concertService, SeatService seatService, ConcertScheduleService concertScheduleService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.userPointHistoryService = userPointHistoryService;
        this.concertService = concertService;
        this.seatService = seatService;
        this.concertScheduleService = concertScheduleService;
    }

    @Transactional
    public Long execute(Long reservationId, String userId) {
        Reservation reservation = reservationService.getReservationInfo(reservationId);

        Seat seat = seatService.getSeatInfo(reservation.getSeatId());
        ConcertSchedule concertSchedule = concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId());
        Concert concert = concertService.getConcertInfo(concertSchedule.getConcertId());

        User user = userService.getUserInfo(userId);
        Long userPoints = user.getPoints();
        Long totalAmount = concert.getConcertPrice();

        Long usePoint = userPoints - totalAmount;

        userService.updateUsePoints(userId, usePoint);

        userPointHistoryService.updateUsePointsHistory(userId, usePoint);

        reservationService.paidReservationStatus(reservationId);

        return reservationId;
    }
}