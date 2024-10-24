package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserInfo(String userId) {
        logger.info("Fetching user info for userId: {}", userId);

        UserEntity userEntity = userRepository.getUserInfo(userId)
                .orElseThrow(() -> {
                    logger.error("User info not found for userId: {}", userId);
                    return new IllegalArgumentException("사용자 정보를 찾을 수가 없습니다.");
                });

        User user = convertToDomain(userEntity);
        logger.debug("User info retrieved: {}", user);
        return user;
    }

    public User addUser(String userId) {
        logger.info("Adding new user with userId: {}", userId);

        User user = addUserDB(userId, 0L);
        logger.info("User added successfully with userId: {}", userId);
        return user;
    }

    public User updateRechargePoints(String userId, long amount) {
        logger.info("Updating recharge points for userId: {}, amount: {}", userId, amount);

        addUserDB(userId, amount);
        logger.info("Recharge points updated for userId: {}", userId);
        return getUserInfo(userId);
    }

    public User updateUsePoints(String userId, long amount) {
        logger.info("Updating use points for userId: {}, amount: {}", userId, amount);
        User user = getUserInfo(userId);

        if (user.getPoints() < Math.abs(amount)) {
            logger.warn("Insufficient points for userId: {}", userId);
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        addUserDB(userId, -Math.abs(amount));
        logger.info("Use points updated for userId: {}", userId);
        return getUserInfo(userId);
    }

    public User addUserDB(String userId, Long amount) {
        logger.debug("Adding user to database with userId: {}, amount: {}", userId, amount);

        UserEntity uploadUserEntity = UserEntity.builder()
                .userId(userId)
                .points(amount)
                .build();
        UserEntity userEntity = userRepository.changeUserInfo(uploadUserEntity);

        User user = convertToDomain(userEntity);
        logger.debug("User added to database successfully: {}", user);
        return user;
    }

    private User convertToDomain(UserEntity userEntity) {
        User user = new User();
        user.setUserId(userEntity.getUserId());
        user.setPoints(userEntity.getPoints());
        return user;
    }

}
