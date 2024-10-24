package io.hhplus.concert.interfaces.api.reservation;

import io.hhplus.concert.application.concert.ReserveSeatUseCase;
import io.hhplus.concert.application.reservation.PaymentUseCase;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.interfaces.dto.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final PaymentUseCase paymentUseCase;

    public ReservationController(ReserveSeatUseCase reserveSeatUseCase, PaymentUseCase paymentUseCase) {
        this.reserveSeatUseCase = reserveSeatUseCase;
        this.paymentUseCase = paymentUseCase;
    }

    // 1. 좌석 예약 API
    @PostMapping("/reserve/{seatNumber}/{concertScheduleId}")
    public ResponseEntity<ReservationResponse> reserveSeat(@PathVariable Long seatNumber, @PathVariable Long concertScheduleId, @RequestHeader("Authorization") String authorizationHeader) {

        if (seatNumber <= 0 || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userId = authorizationHeader.substring(7);

        try {
            Reservation reservation = reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);
            ReservationResponse response = new ReservationResponse(reservation.getReservationId(), "좌석 예약이 완료되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 2. 결제 완료 API
    @PostMapping("/payment/{reservationId}")
    public ResponseEntity<ReservationResponse> payment(@PathVariable Long reservationId, @RequestHeader("Authorization") String authorizationHeader) {
        if (reservationId <= 0 || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userId = authorizationHeader.substring(7);

        try {
            paymentUseCase.execute(reservationId, userId);
            ReservationResponse response = new ReservationResponse(reservationId, "Payment successful");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}