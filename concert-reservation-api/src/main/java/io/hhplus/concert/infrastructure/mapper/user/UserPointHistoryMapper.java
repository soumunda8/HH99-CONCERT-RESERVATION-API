package io.hhplus.concert.infrastructure.mapper.user;

import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.UserPointHistory;
import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;

public class UserPointHistoryMapper {

    public static UserPointHistory toDomain(UserPointHistoryEntity entity) {
        return new UserPointHistory(
                entity.getUserHistoryId(),
                entity.getUserId(),
                PointActionType.valueOf(entity.getActionType()),
                entity.getChangedPoint()
        );
    }

    public static UserPointHistoryEntity toEntity(UserPointHistory userPointHistory) {
        return UserPointHistoryEntity.builder()
                .userHistoryId(userPointHistory.userHistoryId())
                .userId(userPointHistory.userId())
                .actionType(userPointHistory.actionType().name())
                .changedPoint(userPointHistory.changedPoint())
                .build();
    }
}