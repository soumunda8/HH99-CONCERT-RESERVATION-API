package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import io.hhplus.concert.infrastructure.mapper.user.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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
        return addUserDB(userId, 10L);
    }

    public User updateRechargePoints(String userId, long amount) {
        User user = getUserInfo(userId);
        user.addPoints(Math.abs(amount));
        saveUserToDatabase(user);
        return user;
    }

    public User updateUsePoints(String userId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Points to use must be positive.");
        }
        User user = getUserInfo(userId);
        user.usePoints(Math.abs(amount));
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

    /**
     * 비관적 락을 사용하여 포인트를 충전합니다.
     * @param userId 사용자 ID
     * @param points 충전할 포인트
     * @return 충전에 걸린 시간 (밀리초)
     */
    @Transactional
    public long rechargePointsWithPessimisticLock(String userId, long points) {
        Instant start = Instant.now();

        UserEntity userEntity = userRepository.getUserInfoForPessimisticLock(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User user = UserMapper.toDomain(userEntity);
        user.addPoints(points);
        userRepository.addUser(userEntity);

        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    /**
     * 낙관적 락을 사용하여 포인트를 충전합니다.
     * @param userId 사용자 ID
     * @param points 충전할 포인트
     * @return 충전에 걸린 시간 (밀리초)
     */
    @Transactional
    public long rechargePointsWithOptimisticLock(String userId, long points) {
        Instant start = Instant.now();

        UserEntity userEntity = userRepository.getUserInfoForOptimisticLock(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User user = UserMapper.toDomain(userEntity);
        user.addPoints(points);
        userRepository.addUser(userEntity);

        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

}