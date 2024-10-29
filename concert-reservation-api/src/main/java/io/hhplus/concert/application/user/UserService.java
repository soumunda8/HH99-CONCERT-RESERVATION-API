package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import io.hhplus.concert.infrastructure.mapper.user.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserInfo(String userId) {
        UserEntity userEntity = userRepository.getUserInfo(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수가 없습니다."));
        return UserMapper.toDomain(userEntity);
    }

    public User addUser(String userId) {
        return addUserDB(userId, 0L);
    }

    public User updateRechargePoints(String userId, long amount) {
        User user = getUserInfo(userId);
        user.addPoints(amount);
        saveUserToDatabase(user);
        return user;
    }

    public User updateUsePoints(String userId, long amount) {
        User user = getUserInfo(userId);
        user.usePoints(amount);
        saveUserToDatabase(user);
        return user;
    }

    @Transactional
    public User addUserDB(String userId, Long amount) {
        Optional<UserEntity> existingUserEntity = userRepository.getUserInfoForUpdate(userId);

        User user;
        if (existingUserEntity.isPresent()) {
            user = UserMapper.toDomain(existingUserEntity.get());
            user.addPoints(amount);
        } else {
            user = new User(userId, amount);
        }

        saveUserToDatabase(user);
        return user;
    }

    private void saveUserToDatabase(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        userRepository.changeUserInfo(userEntity);
    }
}