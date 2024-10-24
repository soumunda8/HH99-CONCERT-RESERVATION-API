package io.hhplus.concert.interfaces.api.reservation;

import io.hhplus.concert.application.concert.ReserveSeatUseCase;
import io.hhplus.concert.application.reservation.PaymentUseCase;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.interfaces.dto.ReservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final PaymentUseCase paymentUseCase;

    public ReservationController(ReserveSeatUseCase reserveSeatUseCase, PaymentUseCase paymentUseCase) {
        this.reserveSeatUseCase = reserveSeatUseCase;
        this.paymentUseCase = paymentUseCase;
    }

    // 1. 좌석 예약 API
    @PostMapping("/reserve/{seatNumber}/{concertScheduleId}")
    public ResponseEntity<ReservationResponse> reserveSeat(@PathVariable Long seatNumber, @PathVariable Long concertScheduleId, @RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Received request to reserve seatNumber: {} for concertScheduleId: {}", seatNumber, concertScheduleId);

        if (seatNumber <= 0 || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized request to reserve seatNumber: {} for concertScheduleId: {} by userId", seatNumber, concertScheduleId);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userId = authorizationHeader.substring(7);

        try {
            Reservation reservation = reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);
            ReservationResponse response = new ReservationResponse(reservation.getReservationId(), "좌석 예약이 완료되었습니다.");
            logger.info("Successfully reserved seatNumber: {} for concertScheduleId: {} by userId: {}", seatNumber, concertScheduleId, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to reserve seatNumber: {} for concertScheduleId: {}, reason: {}", seatNumber, concertScheduleId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 2. 결제 완료 API
    @PostMapping("/payment/{reservationId}")
    public ResponseEntity<ReservationResponse> payment(@PathVariable Long reservationId, @RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Received payment request for reservationId: {}", reservationId);

        if (reservationId <= 0 || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized payment request for reservationId: {}", reservationId);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userId = authorizationHeader.substring(7);

        try {
            paymentUseCase.execute(reservationId, userId);
            ReservationResponse response = new ReservationResponse(reservationId, "Payment successful");
            logger.info("Payment successful for reservationId: {} by userId: {}", reservationId, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Payment failed for reservationId: {}, reason: {}", reservationId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}