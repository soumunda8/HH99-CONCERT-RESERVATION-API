package io.hhplus.concert.interfaces.api.mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock")
public class MockController {

    // 1. 사용자 등록 API
    @PostMapping("/user/{userId}/{concertId}")
    public ResponseEntity<Void> registerUser(@PathVariable String userId, @PathVariable String concertId) {
        if (userId.isEmpty() || concertId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 2. 큐 상태 확인 API
    @GetMapping("/queue/{userId}/{concertId}")
    public ResponseEntity<Object> checkQueueStatus(@PathVariable String userId, @PathVariable String concertId) {
        if (userId.isEmpty() || concertId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userId.equals("testUserId") && concertId.equals("testConcertId")) {
            return ResponseEntity.ok().body("{ \"queueId\": \"some-queue-id\" }");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"No queue with state 'SUCCESS' found for this user\" }");
    }

    // 3. 예약 가능한 날짜 조회 API
    @GetMapping("/concertDate/{concertScheduleId}")
    public ResponseEntity<Object> getConcertDate(@PathVariable int concertScheduleId) {
        if (concertScheduleId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (concertScheduleId == 12) {
            return ResponseEntity.ok().body("{ \"concertScheduleId\": 12, \"concertId\": 12, \"availableReservationDate\": \"2024-10-20\", \"concertStartTime\": \"18:00\", \"concertEndTime\": \"20:00\", \"maxSeatCount\": 100, \"remainingSeatCount\": 50 }");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"No available reservation dates found\" }");
    }

    // 4. 좌석 정보 조회 API
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

    // 5. 좌석 예약 API
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

    // 6. 결제 완료 API
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

    // 7. 포인트 조회/충전 API
    @GetMapping("/points/{userId}/{actionType}/{amount}")
    public ResponseEntity<Object> managePoints(@PathVariable String userId, @PathVariable String actionType, @PathVariable int amount) {
        if (userId.isEmpty() || (!actionType.equals("check") && !actionType.equals("recharge"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"InvalidRequest\", \"message\": \"Invalid actionType or missing/invalid amount for recharge.\" }");
        }
        if (actionType.equals("check")) {
            return ResponseEntity.ok().body("{ \"userId\": \"" + userId + "\", \"currentPoints\": 1000 }");
        } else {
            return ResponseEntity.ok().body("{ \"userId\": \"" + userId + "\", \"currentPoints\": 1000 + amount }");
        }
    }

}