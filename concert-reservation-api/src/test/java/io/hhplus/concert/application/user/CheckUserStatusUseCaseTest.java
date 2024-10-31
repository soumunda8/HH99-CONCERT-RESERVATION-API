package io.hhplus.concert.application.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class CheckUserStatusUseCaseTest {

    @Mock
    private UserQueueService userQueueService;

    @InjectMocks
    private CheckUserStatusUseCase checkUserStatusUseCase;

    private final String activeUserId = "activeUser";
    private final String inactiveUserId = "inactiveUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_Success_UserIsActive() {
        // given
        given(userQueueService.isUserActive(activeUserId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> checkUserStatusUseCase.execute(activeUserId));
        verify(userQueueService, times(1)).isUserActive(activeUserId);
    }

    @Test
    void execute_Failure_UserIsNotActive() {
        // given
        given(userQueueService.isUserActive(inactiveUserId)).willReturn(false);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> checkUserStatusUseCase.execute(inactiveUserId));
        assertEquals("사용자가 활성 상태가 아닙니다.", exception.getMessage());
        verify(userQueueService, times(1)).isUserActive(inactiveUserId);
    }

}