package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageUserPointsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ManageUserPointsUseCase.class);

    private final UserService userService;
    private final UserPointHistoryService userPointHistoryService;

    public ManageUserPointsUseCase(UserService userService, UserPointHistoryService userPointHistoryService) {
        this.userService = userService;
        this.userPointHistoryService = userPointHistoryService;
    }

    @Transactional
    public User execute(String userId, String actionType, long amount) {
        logger.info("Executing user point management for userId: {}, actionType: {}, amount: {}", userId, actionType, amount);

        if (actionType.equals("check")) {
            logger.info("Checking points for userId: {}", userId);
            return userService.getUserInfo(userId);
        } else if (actionType.equals(PointActionType.CHARGE.name())) {
            logger.info("Charging points for userId: {} with amount: {}", userId, amount);
            User user = userService.updateRechargePoints(userId, amount);
            userPointHistoryService.updateChargePointsHistory(userId, amount);
            logger.info("Points charged successfully for userId: {}", userId);
            return user;
        } else if (actionType.equals(PointActionType.USE.name())) {
            logger.info("Using points for userId: {} with amount: {}", userId, amount);
            User user = userService.updateUsePoints(userId, amount);
            userPointHistoryService.updateUsePointsHistory(userId, amount);
            logger.info("Points used successfully for userId: {}", userId);
            return user;
        }

        logger.warn("Unsupported actionType: {} for userId: {}", actionType, userId);
        throw new IllegalArgumentException("지원되지 않는 actionType입니다.");
    }

}
