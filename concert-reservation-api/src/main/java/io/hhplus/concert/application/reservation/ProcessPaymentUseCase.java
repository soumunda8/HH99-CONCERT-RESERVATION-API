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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessPaymentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentUseCase.class);

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
        logger.info("Processing payment for reservationId: {}, userId: {}", reservationId, userId);

        Reservation reservation = reservationService.getReservationInfo(reservationId);
        logger.debug("Reservation info retrieved: {}", reservation);

        Seat seat = seatService.getSeatInfo(reservation.getSeatId());
        logger.debug("Seat info retrieved: {}", seat);

        ConcertSchedule concertSchedule = concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId());
        logger.debug("Concert schedule info retrieved: {}", concertSchedule);

        Concert concert = concertService.getConcertInfo(concertSchedule.getConcertId());
        logger.debug("Concert info retrieved: {}", concert);

        User user = userService.getUserInfo(userId);
        Long userPoints = user.getPoints();
        Long totalAmount = concert.getConcertPrice();
        logger.debug("User points: {}, Concert price: {}", userPoints, totalAmount);

        Long usePoint = userPoints - totalAmount;
        if (usePoint < 0) {
            logger.warn("Insufficient points for userId: {}. Required: {}, Available: {}", userId, totalAmount, userPoints);
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        userService.updateUsePoints(userId, usePoint);
        logger.info("Points updated for userId: {}. New balance: {}", userId, usePoint);

        userPointHistoryService.updateUsePointsHistory(userId, usePoint);
        logger.info("Point history updated for userId: {}", userId);

        reservationService.paidReservationStatus(reservationId);
        logger.info("Reservation status updated to PAID for reservationId: {}", reservationId);

        return reservationId;
    }
}