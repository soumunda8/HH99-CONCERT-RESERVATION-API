package io.hhplus.concert.domain.user;

import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;

public interface UserPointHistoryRepository {

    void addInfo(UserPointHistoryEntity userPointHistoryEntity);

}