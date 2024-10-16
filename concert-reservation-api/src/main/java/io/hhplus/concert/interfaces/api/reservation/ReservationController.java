package io.hhplus.concert.interfaces.api.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    // 1. 좌석 예약 API
    @PostMapping("/reserve/{seatId}")
    public ResponseEntity<Object> reserveSeat(@PathVariable int seatId, @RequestHeader("Authorization") String queueId) {
        if (seatId <= 0 || queueId == null || !queueId.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (seatId == 1) {
            return ResponseEntity.ok().body("{ \"reservationId\": 123 }");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"SeatUnavailable\", \"message\": \"The seat is already reserved or unavailable.\" }");
    }

    // 2. 결제 완료 API
    @PostMapping("/payment/{reservationId}")
    public ResponseEntity<Object> payment(@PathVariable int reservationId) {
        if (reservationId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (reservationId == 123) {
            return ResponseEntity.ok().body("{ \"reservationId\": 123 }");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"ReservationNotFound\", \"message\": \"No reservation found with the provided reservationId.\" }");
    }

}