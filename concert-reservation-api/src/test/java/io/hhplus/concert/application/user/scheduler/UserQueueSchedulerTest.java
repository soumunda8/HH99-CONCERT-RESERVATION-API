package io.hhplus.concert.application.user.scheduler;

import io.hhplus.concert.application.user.UserQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserQueueSchedulerTest {

    @Mock
    private UserQueueService userQueueService;

    @InjectMocks
    private UserQueueScheduler userQueueScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // 성공 케이스
    @Test
    void successUserQueueScheduler() {
        userQueueScheduler.execute();

        verify(userQueueService, times(1)).expireUserQueues();
        verify(userQueueService, times(1)).activateStandbyUsers();
    }

    // 실패 케이스 01 - expireUserQueues 호출 시 예외 발생
    @Test
    void failExpireQueues() {
        // Given
        doThrow(new RuntimeException("expireUserQueues failed")).when(userQueueService).expireUserQueues();

        // When
        try {
            userQueueScheduler.execute();
        } catch (Exception e) {
            // Then
            assertEquals("expireUserQueues failed", e.getMessage());
        }

        verify(userQueueService, times(1)).activateStandbyUsers();
    }

    // 실패 케이스 02 - activateStandbyUsers 호출 시 예외 발생
    @Test
    void failActivateStandbyUsers() {
        // Given
        doThrow(new RuntimeException("activateStandbyUsers failed")).when(userQueueService).activateStandbyUsers();

        // When
        try {
            userQueueScheduler.execute();
        } catch (Exception e) {
            // Then
            assertEquals("activateStandbyUsers failed", e.getMessage());
        }

        verify(userQueueService, times(1)).expireUserQueues();
    }

}