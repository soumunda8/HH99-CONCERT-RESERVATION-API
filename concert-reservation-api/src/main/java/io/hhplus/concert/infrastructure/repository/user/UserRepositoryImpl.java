package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public UserEntity addUser(UserEntity userEntity) {
        return jpaUserRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> getUserInfo(String userId) {
        return jpaUserRepository.findByUserId(userId);
    }

    @Override
    public UserEntity changeUserInfo(UserEntity userEntity) {
        return jpaUserRepository.save(userEntity);
    }

}