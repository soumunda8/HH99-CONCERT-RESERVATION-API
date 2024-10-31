package io.hhplus.concert.integration.user;

import io.hhplus.concert.application.user.*;
import io.hhplus.concert.domain.user.*;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserIntegrationTest {

    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManageUserPointsUseCase manageUserPointsUseCase;

    @Autowired
    private CheckUserStatusUseCase checkUserStatusUseCase;

    @Autowired
    private AddUserToQueueUseCase addUserToQueueUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQueueRepository userQueueRepository;

    private final String userId = "user123";
    private final long amount = 100L;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 데이터 초기화
        userQueueRepository.removeAllData(); // 모든 UserQueueEntity 레코드 삭제
        userRepository.removeAllData(); // 모든 UserEntity 레코드 삭제

        // 테스트 실행 전 초기 설정
        UserEntity userEntity = UserEntity.builder()
                .userId(userId)
                .points(500L)
                .build();
        userRepository.addUser(userEntity);

        UserQueueEntity userQueueEntity = UserQueueEntity.builder()
                    .userId(userId)
                    .queueStatus(QueueStatus.ACTIVE.name())
                    .queueExpireAt(LocalDateTime.now().plusMinutes(10))
                    .build();
        userQueueRepository.addUserQueue(userQueueEntity);
    }

    @Test
    void testGetUserInfo() {
        // Given - UserService에서 유저 정보를 가져오는지 확인
        User user = userService.getUserInfo(userId);

        // Then
        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals(500L, user.getPoints());
    }

    @Test
    void testAddUserToQueue() {
        // Given - AddUserToQueueUseCase 실행
        addUserToQueueUseCase.execute("newUser");

        // Then
        Optional<UserQueueEntity> userQueue = userQueueRepository.getQueueInfo("newUser");
        assertTrue(userQueue.isPresent());
        assertEquals(QueueStatus.STANDBY.name(), userQueue.get().getQueueStatus());
    }

    @Test
    void testManageUserPoints_Charge() {
        // Given - 포인트 충전 테스트
        User updatedUser = manageUserPointsUseCase.execute(userId, PointActionType.CHARGE.name(), amount);

        // Then
        assertEquals(600L, updatedUser.getPoints());
    }

    @Test
    void testManageUserPoints_Use() {
        // Given - 포인트 사용 테스트
        User updatedUser = manageUserPointsUseCase.execute(userId, PointActionType.USE.name(), amount);

        // Then
        assertEquals(400L, updatedUser.getPoints());
    }

    @Test
    @Transactional
    void testUserQueueScheduler() {
        // Given
        UserQueueEntity activeUserQueue = UserQueueEntity.builder()
                        .userId(userId)
                        .queueStatus(QueueStatus.ACTIVE.name())
                        .queueExpireAt(LocalDateTime.now().minusMinutes(10))
                        .build();
        userQueueRepository.addUserQueue(activeUserQueue);

        // When
        userQueueService.expireUserQueues();

        // Then
        UserQueueEntity updatedQueue = userQueueRepository.getQueueInfoById(activeUserQueue.getQueueId()).orElse(null);
        assertNotNull(updatedQueue);
        assertEquals(QueueStatus.EXPIRE.name(), updatedQueue.getQueueStatus());
    }

}