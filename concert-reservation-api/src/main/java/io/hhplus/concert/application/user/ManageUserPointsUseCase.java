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
        logger.info("Starting point management for userId: {}, actionType: {}", userId, actionType);

        switch (actionType) {
            case "check":
                return userService.getUserInfo(userId);

            case "CHARGE":
                User chargedUser = userService.updateRechargePoints(userId, amount);
                userPointHistoryService.updateChargePointsHistory(userId, amount);
                logger.info("Points charged for userId: {}", userId);
                return chargedUser;

            case "USE":
                User updatedUser = userService.updateUsePoints(userId, amount);
                userPointHistoryService.updateUsePointsHistory(userId, amount);
                logger.info("Points used for userId: {}", userId);
                return updatedUser;

            default:
                logger.warn("Unsupported actionType: {} for userId: {}", actionType, userId);
                throw new IllegalArgumentException("지원되지 않는 actionType입니다.");
        }
    }

}