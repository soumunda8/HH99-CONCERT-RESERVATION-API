package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.UserPointHistory;
import io.hhplus.concert.domain.user.UserPointHistoryRepository;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserPointHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(UserPointHistoryService.class);

    private final UserPointHistoryRepository userPointHistoryRepository;

    public UserPointHistoryService(UserPointHistoryRepository userPointHistoryRepository) {
        this.userPointHistoryRepository = userPointHistoryRepository;
    }

    public void updateChargePointsHistory(String userId, long amount) {
        logger.info("Updating charge points history for userId: {}, amount: {}", userId, amount);

        addUserPointHistory(userId, PointActionType.CHARGE, amount);
        logger.info("Recharge points history updated for userId: {}", userId);
    }

    public void updateUsePointsHistory(String userId, long amount) {
        logger.info("Updating use points history for userId: {}, amount: {}", userId, amount);

        addUserPointHistory(userId, PointActionType.USE, -amount);
        logger.info("Use points history updated for userId: {}", userId);
    }

    public void addUserPointHistory(String userId, PointActionType pointActionType, long amount) {
        logger.debug("Adding user point history for userId: {}, actionType: {}, amount: {}", userId, pointActionType, amount);

        UserPointHistoryEntity pointHistory = UserPointHistoryEntity.builder()
                .userId(userId)
                .actionType(pointActionType.name())
                .changedPoint(amount)
                .build();

        userPointHistoryRepository.addInfo(pointHistory);
        logger.debug("User point history added successfully for userId: {}", userId);
    }

}