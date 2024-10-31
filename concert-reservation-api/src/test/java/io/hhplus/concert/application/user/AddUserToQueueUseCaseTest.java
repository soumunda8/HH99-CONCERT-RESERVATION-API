package io.hhplus.concert.application.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.*;

public class AddUserToQueueUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private UserQueueService userQueueService;

    @InjectMocks
    private AddUserToQueueUseCase addUserToQueueUseCase;

    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_Success_UserAddedToQueue() {
        // given
        given(userQueueService.isUserInExpiredQueue(userId)).willReturn(false);
        given(userQueueService.isUserInQueue(userId)).willReturn(false);

        // when
        addUserToQueueUseCase.execute(userId);

        // then
        verify(userService, times(1)).addUser(userId);
        verify(userQueueService, times(1)).addUserToQueue(userId);
        verify(userQueueService, never()).standbyUserQueueToken(userId);
    }

    @Test
    void execute_UserInExpiredQueue_StandbyUpdated() {
        // given
        given(userQueueService.isUserInExpiredQueue(userId)).willReturn(true);
        given(userQueueService.isUserInQueue(userId)).willReturn(false);

        // when
        addUserToQueueUseCase.execute(userId);

        // then
        verify(userQueueService, times(1)).standbyUserQueueToken(userId);
        verify(userService, times(1)).addUser(userId);
        verify(userQueueService, times(1)).addUserToQueue(userId);
    }

    @Test
    void execute_UserAlreadyInQueue_NoAction() {
        // given
        given(userQueueService.isUserInExpiredQueue(userId)).willReturn(false);
        given(userQueueService.isUserInQueue(userId)).willReturn(true);

        // when
        addUserToQueueUseCase.execute(userId);

        // then
        verify(userService, never()).addUser(anyString());
        verify(userQueueService, never()).addUserToQueue(anyString());
        verify(userQueueService, never()).standbyUserQueueToken(anyString());
    }

}