package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageUserPointsUseCase {

    private final UserService userService;
    private final UserPointHistoryService userPointHistoryService;

    public ManageUserPointsUseCase(UserService userService, UserPointHistoryService userPointHistoryService) {
        this.userService = userService;
        this.userPointHistoryService = userPointHistoryService;
    }

    @Transactional
    public User execute(String userId, String actionType, long amount) {
        if (actionType.equals("check")) {
            return userService.getUserInfo(userId);
        }

        else if (actionType.equals(PointActionType.CHARGE.name())) {
            User user = userService.updateRechargePoints(userId, amount);
            userPointHistoryService.updateRechargePointsHistory(userId, amount);
            return user;
        }

        else if (actionType.equals(PointActionType.USE.name())) {
            User user = userService.updateUsePoints(userId, amount);
            userPointHistoryService.updateUsePointsHistory(userId, amount);
            return user;
        }

        throw new IllegalArgumentException("지원되지 않는 actionType입니다.");
    }

}
