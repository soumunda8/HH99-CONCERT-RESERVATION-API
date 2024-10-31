package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.concert.ConcertScheduleService;
import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.application.concert.SeatService;
import io.hhplus.concert.application.user.UserPointHistoryService;
import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.domain.user.PointActionType;
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

        // 예약 정보 및 좌석 정보 조회
        Reservation reservation = reservationService.getReservationInfo(reservationId);
        Seat seat = seatService.getSeatInfo(reservation.getSeatId());

        // 좌석 상태가 HELD인지 확인
        if (!seat.getSeatStatus().equals(SeatStatus.HELD)) {
            logger.warn("Seat status is not HELD for seatId: {}", seat.getSeatId());
            throw new IllegalStateException("결제를 진행할 수 없는 좌석 상태입니다.");
        }

        ConcertSchedule concertSchedule = concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId());
        Concert concert = concertService.getConcertInfo(concertSchedule.getConcertId());

        User user = userService.getUserInfo(userId);
        Long userPoints = user.getPoints();
        Long totalAmount = concert.getConcertPrice();

        if (userPoints < totalAmount) {
            logger.warn("Insufficient points for userId: {}. Required: {}, Available: {}", userId, totalAmount, userPoints);
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        userService.updateUsePoints(userId, totalAmount);
        userPointHistoryService.updatePointsHistory(userId, PointActionType.USE,totalAmount);
        reservationService.paidReservationStatus(reservationId);

        logger.info("Payment processed successfully for reservationId: {}, userId: {}. Remaining points: {}", reservationId, userId, userPoints - totalAmount);

        return reservationId;
    }

}