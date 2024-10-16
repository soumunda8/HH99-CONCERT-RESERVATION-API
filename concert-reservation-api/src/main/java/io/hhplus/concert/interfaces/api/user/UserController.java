package io.hhplus.concert.interfaces.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // 1. 사용자 등록 API
    @PostMapping("/{userId}/{concertId}")
    public ResponseEntity<Void> registerUser(@PathVariable String userId, @PathVariable String concertId) {

        if (userId.isEmpty() || concertId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 2. 포인트 조회/충전 API
    @PostMapping("/points/{userId}")
    public ResponseEntity<Object> managePoints(@PathVariable String userId, @RequestParam String actionType, @RequestParam int amount) {

        if (userId.isEmpty() || (!actionType.equals("check") && !actionType.equals("recharge"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"InvalidRequest\", \"message\": \"Invalid actionType or missing/invalid amount for recharge.\" }");
        }

        if (actionType.equals("check")) {
            return ResponseEntity.ok().body("{ \"userId\": \"" + userId + "\", \"currentPoints\": 1000 }");
        } else if (actionType.equals("recharge")) {
            return ResponseEntity.ok().body("{ \"userId\": \"" + userId + "\", \"currentPoints\": " + (1000 + amount) + " }");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"InvalidRequest\", \"message\": \"Action not supported.\" }");
    }

    // 3. 큐 상태 확인 API
    @GetMapping("/queue/{userId}")
    public ResponseEntity<Object> addToQueueAndCheckStatus(@PathVariable String userId) {
        if (userId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userId.equals("testUserId")) {
            return ResponseEntity.ok().body("{ \"queueId\": \"some-queue-id\", \"queueStatus\": \"STANDBY\" }");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"No queue with state 'STANDBY' found for this user\" }");
    }

}