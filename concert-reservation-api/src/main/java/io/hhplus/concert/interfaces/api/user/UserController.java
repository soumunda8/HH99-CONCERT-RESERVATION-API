package io.hhplus.concert.interfaces.api.user;

import io.hhplus.concert.application.user.*;
import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.interfaces.dto.PointsResponse;
import io.hhplus.concert.interfaces.dto.QueueStatusResponse;
import io.hhplus.concert.utils.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AddUserToQueueUseCase addUserToQueueUseCase;
    private final ManageUserPointsUseCase manageUserPointsUseCase;
    private final CheckUserStatusUseCase checkUserStatusUseCase;
    private final CountUserQueueUseCase countUserQueueUseCase;
    private final UserService userService;

    public UserController(AddUserToQueueUseCase addUserToQueueUseCase, ManageUserPointsUseCase manageUserPointsUseCase, CheckUserStatusUseCase checkUserStatusUseCase, CountUserQueueUseCase countUserQueueUseCase, UserService userService) {
        this.addUserToQueueUseCase = addUserToQueueUseCase;
        this.manageUserPointsUseCase = manageUserPointsUseCase;
        this.checkUserStatusUseCase = checkUserStatusUseCase;
        this.countUserQueueUseCase = countUserQueueUseCase;
        this.userService = userService;
    }

    // 1. 대기열 등록 API
    @PostMapping("/{userId}")
    public ResponseEntity<Void> addQueue(@PathVariable String userId) {
        ValidationUtils.validateUserId(userId);
        addUserToQueueUseCase.execute(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 2. 포인트 조회/충전 API
    @PostMapping("/points/{userId}")
    public ResponseEntity<PointsResponse> managePoints(@PathVariable String userId, @RequestParam String actionType, @RequestParam(defaultValue = "0") int amount) {
        ValidationUtils.validateUserId(userId);

        if (actionType.equals("") || actionType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            User user = manageUserPointsUseCase.execute(userId, actionType, amount);
            PointsResponse response = new PointsResponse(userId, actionType, amount, user.getPoints());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 3. 대기열 상태 확인 API
    @GetMapping("/queue/{userId}")
    public ResponseEntity<QueueStatusResponse> checkQueueStatus(@PathVariable String userId) {
        try {
            Long position = countUserQueueUseCase.execute(userId);
            QueueStatusResponse response = new QueueStatusResponse(userId, position);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}