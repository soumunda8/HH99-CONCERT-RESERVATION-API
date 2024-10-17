package io.hhplus.concert.interfaces.api.user;

import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.utils.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. 대기열 등록 API
    @PostMapping("/{userId}")
    public ResponseEntity<Void> addQueue(@PathVariable String userId) {
        ValidationUtils.validateUserId(userId);
        userService.addQueue(userId);
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

    // 3. 대기열 상태 확인 API
    @GetMapping("/queue/{userId}")
    public ResponseEntity<Integer> checkQueueStatus(@PathVariable String userId) {
        ValidationUtils.validateUserId(userId);

        try {
            int count = userService.countQueues(userId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}