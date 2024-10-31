package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueueRepository;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class UserQueueServiceTest {

    @Mock
    private UserQueueRepository userQueueRepository;

    @InjectMocks
    private UserQueueService userQueueService;

    private String userId = "user123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 성공 케이스 - activateStandbyUsers
    @Test
    void activateStandbyUsers_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UserQueueEntity standbyUser = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.STANDBY.name())
                .build();

        given(userQueueRepository.getAllByUserStatusByCreateAt(eq(QueueStatus.STANDBY.name()), any(LocalDateTime.class)))
                .willReturn(List.of(standbyUser));

        UserQueueEntity activeUser = UserQueueEntity.builder()
                .queueId(2L)
                .userId("user456")
                .queueStatus(QueueStatus.ACTIVE.name())
                .build();

        given(userQueueRepository.getAllByUserStatusByCreateAt(eq(QueueStatus.ACTIVE.name()), any(LocalDateTime.class)))
                .willReturn(List.of(activeUser));

        // When
        userQueueService.activateStandbyUsers();

        // Then
        ArgumentCaptor<UserQueueEntity> captor = ArgumentCaptor.forClass(UserQueueEntity.class);
        verify(userQueueRepository, atLeastOnce()).addUserQueue(captor.capture());
        UserQueueEntity updatedEntity = captor.getValue();

        assertEquals(QueueStatus.ACTIVE.name(), updatedEntity.getQueueStatus());
        assertNotNull(updatedEntity.getQueueExpireAt());
    }

    // 성공 케이스 - expireUserQueues
    @Test
    void expireUserQueues_Success() {
        // Given
        UserQueueEntity activeUser = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.ACTIVE.name())
                .queueExpireAt(LocalDateTime.now().minusMinutes(1))
                .build();

        given(userQueueRepository.getTop10UserStatusByExpireAt(eq(QueueStatus.ACTIVE.name()), any(LocalDateTime.class)))
                .willReturn(List.of(activeUser));

        // When
        userQueueService.expireUserQueues();

        // Then
        ArgumentCaptor<UserQueueEntity> captor = ArgumentCaptor.forClass(UserQueueEntity.class);
        verify(userQueueRepository).addUserQueue(captor.capture());
        UserQueueEntity updatedEntity = captor.getValue();

        assertEquals(QueueStatus.EXPIRE.name(), updatedEntity.getQueueStatus());
    }

    // 실패 케이스 - removeUserQueueToken (사용자 없음)
    @Test
    void removeUserQueueToken_Failure_UserNotFound() {
        // Given
        given(userQueueRepository.getQueueInfo(userId)).willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userQueueService.removeUserQueueToken(userId));
        assertEquals("관련한 정보가 없습니다.", exception.getMessage());
        verify(userQueueRepository, never()).removeUserQueueToken(anyLong());
    }

    // 성공 케이스 - addUserToQueue
    @Test
    void addUserToQueue_Success() {
        // When
        userQueueService.addUserToQueue(userId);

        // Then
        ArgumentCaptor<UserQueueEntity> captor = ArgumentCaptor.forClass(UserQueueEntity.class);
        verify(userQueueRepository).addUserQueue(captor.capture());
        UserQueueEntity addedEntity = captor.getValue();

        assertEquals(userId, addedEntity.getUserId());
        assertEquals(QueueStatus.STANDBY.name(), addedEntity.getQueueStatus());
    }

    // 성공 케이스 - isUserInExpiredQueue
    @Test
    void isUserInExpiredQueue_Success() {
        // Given
        UserQueueEntity expiredQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.EXPIRE.name())
                .queueExpireAt(LocalDateTime.now().minusMinutes(5))
                .build();
        given(userQueueRepository.getQueueInfo(userId)).willReturn(Optional.of(expiredQueueEntity));

        // When
        boolean result = userQueueService.isUserInExpiredQueue(userId);

        // Then
        assertTrue(result);
    }

    // 실패 케이스 - isUserInExpiredQueue (만료되지 않은 경우)
    @Test
    void isUserInExpiredQueue_Failure_NotExpired() {
        // Given
        UserQueueEntity activeQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.ACTIVE.name())
                .queueExpireAt(LocalDateTime.now().plusMinutes(5))
                .build();
        given(userQueueRepository.getQueueInfo(userId)).willReturn(Optional.of(activeQueueEntity));

        // When
        boolean result = userQueueService.isUserInExpiredQueue(userId);

        // Then
        assertFalse(result);
    }

    // 성공 케이스 - isUserActive
    @Test
    void isUserActive_Success() {
        // Given
        UserQueueEntity activeQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.ACTIVE.name())
                .build();
        given(userQueueRepository.getQueueInfo(userId)).willReturn(Optional.of(activeQueueEntity));

        // When
        boolean result = userQueueService.isUserActive(userId);

        // Then
        assertTrue(result);
    }

    // 실패 케이스 - isUserActive (활성화 상태가 아닌 경우)
    @Test
    void isUserActive_Failure_NotActive() {
        // Given
        UserQueueEntity standbyQueueEntity = UserQueueEntity.builder()
                .queueId(1L)
                .userId(userId)
                .queueStatus(QueueStatus.STANDBY.name())
                .build();
        given(userQueueRepository.getQueueInfo(userId)).willReturn(Optional.of(standbyQueueEntity));

        // When
        boolean result = userQueueService.isUserActive(userId);

        // Then
        assertFalse(result);
    }

}