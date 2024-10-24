package io.hhplus.concert.domain.user;

import io.hhplus.concert.infrastructure.entity.user.UserEntity;

import java.util.Optional;

public interface UserRepository {

    UserEntity addUser(UserEntity userEntity);
    Optional<UserEntity> getUserInfo(String userId);
    UserEntity changeUserInfo(UserEntity userEntity);
}