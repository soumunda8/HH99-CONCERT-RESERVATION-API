package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserInfo(String userId) {
        UserEntity userEntity = userRepository.getUserInfo(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수가 없습니다."));
        return convertToDomain(userEntity);
    }

    public User addUser(String userId) {
        return addUserDB(userId, 0L);
    }

    public User updateRechargePoints(String userId, long amount) {
        addUserDB(userId, amount);

        return getUserInfo(userId);
    }

    public User updateUsePoints(String userId, long amount) {
        User user = getUserInfo(userId);

        if (user.getPoints() < Math.abs(amount)) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        addUserDB(userId, -Math.abs(amount));

        return getUserInfo(userId);
    }

    public User addUserDB(String userId, Long amount) {
        UserEntity uploadUserEntity = UserEntity.builder()
                .userId(userId)
                .points(amount)
                .build();
        UserEntity userEntity = userRepository.changeUserInfo(uploadUserEntity);
        return convertToDomain(userEntity);
    }

    private User convertToDomain(UserEntity userEntity) {
        User user = new User();
        user.setUserId(userEntity.getUserId());
        user.setPoints(userEntity.getPoints());
        return user;
    }

}
