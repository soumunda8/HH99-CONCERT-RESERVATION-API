package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;
import io.hhplus.concert.infrastructure.mapper.user.UserQueueMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class CountUserQueueUseCaseTest {

    @Mock
    private UserQueueService userQueueService;

    @InjectMocks
    private CountUserQueueUseCase countUserQueueUseCase;

    private final String userId = "testUser";
    private UserQueueEntity userQueueEntity;
    private UserQueue userQueue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime expireAt = LocalDateTime.now().minusMinutes(5);

        userQueueEntity = UserQueueEntity.builder()
                .userId(userId)
                .queueExpireAt(LocalDateTime.now().plusMinutes(10))
                .queueStatus(QueueStatus.ACTIVE.name())
                .queueExpireAt(expireAt)
                .build();

        userQueue = UserQueueMapper.toDomain(userQueueEntity);
    }

    @Test
    void execute_Success() {
        // given
        when(userQueueService.getUserQueueInfo(userId)).thenReturn(userQueue);
        when(userQueueService.countUsersInQueue(userQueue.getCreateAt())).thenReturn(5);

        // when
        int result = countUserQueueUseCase.execute(userId);

        // then
        assertEquals(5, result);
        verify(userQueueService, times(1)).getUserQueueInfo(userId);
        verify(userQueueService, times(1)).countUsersInQueue(userQueue.getCreateAt());
    }

    @Test
    void execute_Failure_InvalidUserId() {
        // given
        when(userQueueService.getUserQueueInfo(userId)).thenThrow(new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> countUserQueueUseCase.execute(userId));
        assertEquals("유효하지 않은 사용자 ID입니다.", exception.getMessage());
        verify(userQueueService, times(1)).getUserQueueInfo(userId);
        verify(userQueueService, never()).countUsersInQueue(any());
    }

}