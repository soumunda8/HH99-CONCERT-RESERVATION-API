package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class ManageUserPointsUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private UserPointHistoryService userPointHistoryService;

    @InjectMocks
    private ManageUserPointsUseCase manageUserPointsUseCase;

    private final String userId = "testUser";
    private final long amount = 100;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_CheckUserPoints_Success() {
        // given
        User user = new User(userId, 500L); // User 객체 생성
        when(userService.getUserInfo(userId)).thenReturn(user);

        // when
        User result = manageUserPointsUseCase.execute(userId, "CHECK", amount);

        // then
        assertEquals(user, result); // User 객체 비교
        verify(userService, times(1)).getUserInfo(userId);
        verifyNoInteractions(userPointHistoryService);
    }

    @Test
    void execute_ChargePoints_Success() {
        // given
        User user = new User(userId, 500L);
        when(userService.getUserInfo(userId)).thenReturn(user);

        // when
        User result = manageUserPointsUseCase.execute(userId, PointActionType.CHARGE.name(), amount);

        // then
        verify(userService, times(1)).updateRechargePoints(userId, amount);
        verify(userPointHistoryService, times(1)).updatePointsHistory(userId, PointActionType.CHARGE, amount);
        assertNotNull(result);
    }

    @Test
    void execute_UsePoints_Success() {
        // given
        User user = new User(userId, 500L);
        when(userService.getUserInfo(userId)).thenReturn(user);

        // when
        User result = manageUserPointsUseCase.execute(userId, PointActionType.USE.name(), amount);

        // then
        verify(userService, times(1)).updateUsePoints(userId, amount);
        verify(userPointHistoryService, times(1)).updatePointsHistory(userId, PointActionType.USE, amount);
        assertNotNull(result);
    }

    @Test
    void execute_InvalidActionType_Failure() {
        // given
        String invalidActionType = "INVALID";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manageUserPointsUseCase.execute(userId, invalidActionType, amount));

        assertEquals("지원되지 않는 actionType입니다.", exception.getMessage());
        verifyNoInteractions(userService, userPointHistoryService);
    }

    @Test
    void execute_UsePoints_InsufficientPoints_Failure() {
        // given
        doThrow(new IllegalStateException("Insufficient points."))
                .when(userService).updateUsePoints(userId, amount);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> manageUserPointsUseCase.execute(userId, "USE", amount));

        assertEquals("Insufficient points.", exception.getMessage());
        verify(userService, times(1)).updateUsePoints(userId, amount);
        verifyNoInteractions(userPointHistoryService);
    }

}