package io.hhplus.concert.infrastructure.mapper.user;

import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getUserId(),
                entity.getPoints()
        );
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .points(user.getPoints())
                .build();
    }

}