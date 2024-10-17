package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
}