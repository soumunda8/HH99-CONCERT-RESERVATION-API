package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.repository.user.UserPointHistoryRepository;
import io.hhplus.concert.domain.repository.user.UserQueueRepository;
import io.hhplus.concert.domain.repository.user.UserRepository;
import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class UserServiceTest {

    private static final String USER_ID = "user1212";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserQueueRepository userQueueRepository;

    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 대기열 실패 케이스 01 - 사용자 상태가 EXPIRE 상태가 아닌 경우
    @Test
    void notExpiredQueueAndNotInQueue() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name())).thenReturn(false);

        when(userQueueRepository.checkIfUserInQueue(userId)).thenReturn(false);

        // When
        userService.addQueue(userId);

        // Then
        verify(userQueueRepository, never()).save(any(UserQueueEntity.class));
    }

    // 대기열 실패 케이스 02 - 대기열 테이블 내 사용자 존재
    @Test
    void isAlreadyInQueue() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name())).thenReturn(false);
        when(userQueueRepository.checkIfUserInQueue(userId)).thenReturn(true);

        // When
        userService.addQueue(userId);

        // Then
        verify(userQueueRepository).save(any(UserQueueEntity.class));
    }

    // 대기열 실패 케이스 03 - 대기열 상태 변경 실패
    @Test
    void failUpdateQueueStatus() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name())).thenReturn(true);

        UserQueueEntity userQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.EXPIRE.name())
                .build();
        when(userQueueRepository.getQueueInfo(userId)).thenReturn(userQueueEntity);

        // When
        userService.addQueue(userId);

        // Then
        verify(userQueueRepository).save(any(UserQueueEntity.class));
    }

    // 폴링용 API 실패 케이스 01 - 대기열 테이블 내 사용자 없음
    @Test
    void userNotFound() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.getQueueInfo(userId)).thenReturn(null);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.countQueues(userId);
        });

        assertEquals("관련한 정보가 없습니다.", exception.getMessage());
    }

    // 폴링용 API 실패 케이스 02 - 데이터베이스 조회 중 에러
    @Test
    void countQueueDatabaseError() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.getQueueInfo(userId)).thenThrow(new RuntimeException("Database error"));

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.countQueues(userId);
        });

        assertEquals("Database error", exception.getMessage());
    }

    // 성공 케이스 01 - 대기열 테이블 내 사용자 추가
    @Test
    void successAddUserToQueue() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name())).thenReturn(false);
        when(userQueueRepository.checkIfUserInQueue(userId)).thenReturn(false);

        // When
        userService.addQueue(userId);

        // Then
        verify(userQueueRepository).save(any(UserQueueEntity.class));
    }

    // 성공 케이스 02 - 대기열 테이블 내 사용자 상태 변경
    @Test
    void successUpdateQueueStatus() {
        // Given
        String userId = USER_ID;

        when(userQueueRepository.checkIfUserInQueueWithStatus(userId, QueueStatus.EXPIRE.name())).thenReturn(true);

        UserQueueEntity userQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.EXPIRE.name())
                .build();
        when(userQueueRepository.getQueueInfo(userId)).thenReturn(userQueueEntity);

        // When
        userService.addQueue(userId);

        // Then
        verify(userQueueRepository).save(any(UserQueueEntity.class));
    }

    // 성공 케이스 03 - 폴링용 API
    @Test
    void successCountQueues() {
        // Given
        String userId = USER_ID;

        UserQueueEntity userQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.STANDBY.name())
                .build();

        when(userQueueRepository.getQueueInfo(userId)).thenReturn(userQueueEntity);
        when(userQueueRepository.countByQueue(userQueueEntity.getCreateAt())).thenReturn(5);

        // When
        int queueCount = userService.countQueues(userId);

        // Then
        assertEquals(5, queueCount);
    }

}
