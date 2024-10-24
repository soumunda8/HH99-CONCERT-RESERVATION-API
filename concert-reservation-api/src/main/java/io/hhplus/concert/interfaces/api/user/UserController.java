package io.hhplus.concert.interfaces.api.user;

import io.hhplus.concert.application.user.*;
import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.interfaces.dto.PointsResponse;
import io.hhplus.concert.interfaces.dto.QueueStatusResponse;
import io.hhplus.concert.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
        logger.info("Received request to add user to queue with userId: {}", userId);
        ValidationUtils.validateUserId(userId);

        addUserToQueueUseCase.execute(userId);
        logger.info("User added to queue successfully with userId: {}", userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 2. 포인트 조회/충전 API
    @PostMapping("/points/{userId}")
    public ResponseEntity<PointsResponse> managePoints(@PathVariable String userId, @RequestParam String actionType, @RequestParam int amount) {
        logger.info("Received request to manage points for userId: {}, actionType: {}, amount: {}", userId, actionType, amount);
        ValidationUtils.validateUserId(userId);

        userService.addUser(userId);

        if (!actionType.equals(PointActionType.USE) && !actionType.equals(PointActionType.CHARGE)) {
            logger.warn("Invalid actionType: {} for userId: {}", actionType, userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            User user = manageUserPointsUseCase.execute(userId, actionType, amount);
            PointsResponse response = new PointsResponse(userId, actionType, amount, user.getPoints());
            logger.info("Points managed successfully for userId: {}, new balance: {}", userId, user.getPoints());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to manage points for userId: {}, reason: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 3. 대기열 상태 확인 API
    @GetMapping("/queue/{userId}/{queueId}")
    public ResponseEntity<QueueStatusResponse> checkQueueStatus(@PathVariable String userId, @PathVariable Long queueId) {
        logger.info("Received request to check queue status for userId: {}, queueId: {}", userId, queueId);

        try {
            int position = countUserQueueUseCase.execute(userId);
            QueueStatusResponse response = new QueueStatusResponse(userId, position);
            logger.info("Queue status checked successfully for userId: {}, position: {}", userId, position);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to check queue status for userId: {}, reason: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

}