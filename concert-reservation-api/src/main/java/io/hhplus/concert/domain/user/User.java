package io.hhplus.concert.domain.user;

import lombok.*;

public class User {

    private final String userId;
    private Long points;

    // 생성자를 통한 초기화
    public User(String userId, Long points) {
        this.userId = userId;
        this.points = points != null ? points : 0L;
    }

    public String getUserId() {
        return userId;
    }

    public Long getPoints() {
        return points;
    }

    // 포인트를 추가하는 의미 있는 메서드
    public void addPoints(Long points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to add must be positive.");
        }
        this.points += points;
    }

    // 포인트를 사용하는 의미 있는 메서드
    public void usePoints(Long points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to use must be positive.");
        }
        if (this.points < points) {
            throw new IllegalStateException("Insufficient points.");
        }
        this.points -= points;
    }
}