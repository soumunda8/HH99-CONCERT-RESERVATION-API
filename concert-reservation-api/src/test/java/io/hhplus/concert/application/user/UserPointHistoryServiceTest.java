package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.UserPointHistoryRepository;
import io.hhplus.concert.infrastructure.entity.user.UserPointHistoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

public class UserPointHistoryServiceTest {

    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    @InjectMocks
    private UserPointHistoryService userPointHistoryService;

    private String userId = "user123";
    private long amount = 1000L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateChargePointsHistory_Success() {
        userPointHistoryService.updatePointsHistory(userId, PointActionType.CHARGE, amount);
        ArgumentCaptor<UserPointHistoryEntity> captor = ArgumentCaptor.forClass(UserPointHistoryEntity.class);
        verify(userPointHistoryRepository, times(1)).addInfo(captor.capture());
        UserPointHistoryEntity savedEntity = captor.getValue();
        assertEquals(userId, savedEntity.getUserId());
        assertEquals(PointActionType.CHARGE.name(), savedEntity.getActionType());
        assertEquals(amount, savedEntity.getChangedPoint());
    }

    @Test
    void updateUsePointsHistory_Success() {
        userPointHistoryService.updatePointsHistory(userId, PointActionType.USE, amount);
        ArgumentCaptor<UserPointHistoryEntity> captor = ArgumentCaptor.forClass(UserPointHistoryEntity.class);
        verify(userPointHistoryRepository, times(1)).addInfo(captor.capture());
        UserPointHistoryEntity savedEntity = captor.getValue();
        assertEquals(userId, savedEntity.getUserId());
        assertEquals(PointActionType.USE.name(), savedEntity.getActionType());
        assertEquals(amount, savedEntity.getChangedPoint());
    }

    @Test
    void updateChargePointsHistory_Failure() {
        doThrow(new RuntimeException("Database error")).when(userPointHistoryRepository).addInfo(any(UserPointHistoryEntity.class));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userPointHistoryService.updatePointsHistory(userId, PointActionType.USE, amount)
        );
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void updateUsePointsHistory_Failure() {
        doThrow(new RuntimeException("Database error")).when(userPointHistoryRepository).addInfo(any(UserPointHistoryEntity.class));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userPointHistoryService.updatePointsHistory(userId, PointActionType.CHARGE, amount)
        );
        assertEquals("Database error", exception.getMessage());
    }

}