package io.hhplus.concert.interfaces.api.reservation;

import io.hhplus.concert.application.facade.ReserveFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReserveFacade reserveFacade;

    public ReservationController(ReserveFacade reserveFacade) {
        this.reserveFacade = reserveFacade;
    }

    // 1. 좌석 예약 API
    @PostMapping("/reserve/{seatNumber}/{concertScheduleId}")
    public ResponseEntity<Long> reserveSeat(@PathVariable Long seatNumber, @PathVariable Long concertScheduleId, @RequestHeader("Authorization") String authorizationHeader) {

        if (seatNumber <= 0 || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userId = authorizationHeader.substring(7);

        Long seatId = reserveFacade.reserveSeat(seatNumber, concertScheduleId, userId);
        return new ResponseEntity<>(seatId, HttpStatus.OK);
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