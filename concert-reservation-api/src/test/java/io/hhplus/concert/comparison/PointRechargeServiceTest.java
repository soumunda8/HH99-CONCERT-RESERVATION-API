package io.hhplus.concert.comparison;

import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointRechargeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final String TEST_USER_ID = "testUser";

    @BeforeEach
    public void setUp() {
        // 사용자 존재 여부 확인 후 없으면 새로 생성
        userRepository.getUserInfo(TEST_USER_ID).orElseGet(() -> {
            UserEntity user = UserEntity.builder()
                    .userId(TEST_USER_ID)
                    .points(0L)
                    .build();
            return userRepository.addUser(user);
        });
    }

    @Test
    public void testPessimisticLockPerformance() {
        long points = 100;
        long timeTaken = userService.rechargePointsWithPessimisticLock(TEST_USER_ID, points);
        System.out.println("Pessimistic Lock Time Taken: " + timeTaken + "ms");
    }

    @Test
    public void testOptimisticLockPerformance() {
        long points = 100;
        long timeTaken = userService.rechargePointsWithOptimisticLock(TEST_USER_ID, points);
        System.out.println("Optimistic Lock Time Taken: " + timeTaken + "ms");
    }

}