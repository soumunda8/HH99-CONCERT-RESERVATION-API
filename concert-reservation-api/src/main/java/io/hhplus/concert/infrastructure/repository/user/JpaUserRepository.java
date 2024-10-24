package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUserId(String userId);
}