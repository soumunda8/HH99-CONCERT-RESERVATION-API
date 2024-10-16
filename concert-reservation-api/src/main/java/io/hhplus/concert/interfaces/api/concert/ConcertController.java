package io.hhplus.concert.interfaces.api.concert;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    // 1. 예약 가능한 날짜 조회 API
    @GetMapping("/dates/{concertScheduleId}")
    public ResponseEntity<Object> getConcertDate(@PathVariable int concertScheduleId) {
        if (concertScheduleId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (concertScheduleId == 12) {
            return ResponseEntity.ok().body("{ \"concertScheduleId\": 12, \"concertId\": 12, \"availableReservationDate\": \"2024-10-20\", \"concertStartTime\": \"18:00\", \"concertEndTime\": \"20:00\", \"maxSeatCount\": 100, \"remainingSeatCount\": 50 }");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"No available reservation dates found\" }");
    }

    // 2. 좌석 정보 조회 API
    @GetMapping("/seats/{concertScheduleId}/{seatNumber}")
    public ResponseEntity<Object> getSeatInfo(@PathVariable int concertScheduleId, @PathVariable int seatNumber) {
        if (concertScheduleId <= 0 || seatNumber <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (concertScheduleId == 12 && seatNumber == 1) {
            return ResponseEntity.ok().body("{ \"seatId\": 1, \"concertScheduleId\": 12, \"seatNumber\": 1, \"status\": \"available\", \"queueId\": \"some-queue-id\", \"createAt\": \"2024-10-10T10:00:00\", \"updateAt\": \"2024-10-10T10:05:00\" }");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"SeatNotFound\", \"message\": \"The seat number is not available for the selected concert.\" }");
    }
}