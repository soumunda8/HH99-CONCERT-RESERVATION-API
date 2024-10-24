package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.UserPointHistory;
import io.hhplus.concert.domain.user.UserPointHistoryRepository;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;
import org.springframework.stereotype.Service;

@Service
public class UserPointHistoryService {

    private final UserPointHistoryRepository userPointHistoryRepository;

    public UserPointHistoryService(UserPointHistoryRepository userPointHistoryRepository) {
        this.userPointHistoryRepository = userPointHistoryRepository;
    }

    public void updateRechargePointsHistory(String userId, long amount) {
        addUserPointHistory(userId, PointActionType.CHARGE, amount);
    }

    public void updateUsePointsHistory(String userId, long amount) {
        addUserPointHistory(userId, PointActionType.USE, -amount);
    }

    public void addUserPointHistory(String userId, PointActionType pointActionType, long amount) {
        UserPointHistoryEntity pointHistory = UserPointHistoryEntity.builder()
                .userId(userId)
                .actionType(pointActionType.name())
                .changedPoint(amount)
                .build();
        userPointHistoryRepository.addInfo(pointHistory);
    }

}