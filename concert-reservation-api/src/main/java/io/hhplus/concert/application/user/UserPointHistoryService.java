package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.UserPointHistory;
import io.hhplus.concert.domain.user.UserPointHistoryRepository;
import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;
import io.hhplus.concert.infrastructure.mapper.user.UserPointHistoryMapper;
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
        logger.info("Recording charge points history for userId: {}", userId);
        saveUserPointHistory(new UserPointHistory(null, userId, PointActionType.CHARGE, amount));
    }

    public void updateUsePointsHistory(String userId, long amount) {
        logger.info("Recording use points history for userId: {}", userId);
        saveUserPointHistory(new UserPointHistory(null, userId, PointActionType.USE, -amount));
    }

    private void saveUserPointHistory(UserPointHistory userPointHistory) {
        UserPointHistoryEntity pointHistoryEntity = UserPointHistoryMapper.toEntity(userPointHistory);
        userPointHistoryRepository.addInfo(pointHistoryEntity);
    }

}