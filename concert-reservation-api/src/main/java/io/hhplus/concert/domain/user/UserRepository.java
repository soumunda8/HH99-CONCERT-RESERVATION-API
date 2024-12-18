package io.hhplus.concert.domain.user;

import io.hhplus.concert.infrastructure.entity.user.UserEntity;

import java.util.Optional;

public interface UserRepository {

    UserEntity addUser(UserEntity userEntity);
    Optional<UserEntity> getUserInfo(String userId);
    Optional<UserEntity> getUserInfoForUpdate(String userId);
    UserEntity changeUserInfo(UserEntity userEntity);
    void removeAllData();

    Optional<UserEntity> getUserInfoForPessimisticLock(String userId);
    Optional<UserEntity> getUserInfoForOptimisticLock(String userId);

}