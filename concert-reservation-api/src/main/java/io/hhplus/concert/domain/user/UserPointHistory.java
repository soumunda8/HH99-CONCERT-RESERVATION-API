package io.hhplus.concert.domain.user;

public record UserPointHistory(Long userHistoryId, String userId, PointActionType actionType, Long changedPoint) {

    // 생성자를 통한 초기화
    public UserPointHistory {
        if (changedPoint == null || changedPoint <= 0) {
            throw new IllegalArgumentException("Changed points must be positive.");
        }
    }

}