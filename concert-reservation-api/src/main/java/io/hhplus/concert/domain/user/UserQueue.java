package io.hhplus.concert.domain.user;

import java.time.LocalDateTime;

public class UserQueue {

    private final Long queueId;
    private final String userId;
    private QueueStatus queueStatus;
    private final LocalDateTime createAt;
    private LocalDateTime queueExpireAt;

    public UserQueue(Long queueId, String userId, QueueStatus queueStatus, LocalDateTime createAt, LocalDateTime queueExpireAt) {
        this.queueId = queueId;
        this.userId = userId;
        this.queueStatus = queueStatus;
        this.createAt = createAt != null ? createAt : LocalDateTime.now(); // 생성 시간 기본값
        this.queueExpireAt = queueExpireAt;
    }

    public Long getQueueId() {
        return queueId;
    }

    public String getUserId() {
        return userId;
    }

    public QueueStatus getQueueStatus() {
        return queueStatus;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getQueueExpireAt() {
        return queueExpireAt;
    }

    // Standby 상태로 변경
    public void standby() {
        this.queueStatus = QueueStatus.STANDBY;
    }

    // 활성화 상태로 변경
    public void activate(LocalDateTime expireAt) {
        if (this.queueStatus != QueueStatus.STANDBY) {
            throw new IllegalStateException("Only standby queues can be activated.");
        }
        this.queueStatus = QueueStatus.ACTIVE;
        this.queueExpireAt = expireAt;
    }

    // 만료 상태로 변경
    public void expire() {
        if (this.queueStatus != QueueStatus.ACTIVE) {
            throw new IllegalStateException("Only active queues can be expired.");
        }
        this.queueStatus = QueueStatus.EXPIRE;
        this.queueExpireAt = LocalDateTime.now(); // 만료 시간 설정
    }

}