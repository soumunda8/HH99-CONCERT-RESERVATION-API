package io.hhplus.concert.infrastructure.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    private String userId;

    @Column(nullable = false)
    private Long points;

    @Builder
    public UserEntity(String userId, Long points) {
        this.userId = userId;
        this.points = points != null ? points : 0L;
    }

}