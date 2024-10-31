package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.domain.user.UserRepository;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String userId = "user123";
    private final long initialPoints = 500L;
    private final long amountToAdd = 100L;
    private final long amountToUse = 100L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserInfo_Success() {
        // Given
        UserEntity userEntity = new UserEntity(userId, initialPoints);
        given(userRepository.getUserInfo(userId)).willReturn(Optional.of(userEntity));

        // When
        User result = userService.getUserInfo(userId);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals(initialPoints, result.getPoints());
    }

    @Test
    void getUserInfo_Failure_UserNotFound() {
        // Given
        given(userRepository.getUserInfo(userId)).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getUserInfo(userId));
        assertEquals("사용자 정보를 찾을 수가 없습니다.", exception.getMessage());
    }

    @Test
    void addUser_Success_NewUser() {
        // Given
        given(userRepository.getUserInfoForUpdate(userId)).willReturn(Optional.empty());

        // When
        User result = userService.addUser(userId);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals(0L, result.getPoints());
        verify(userRepository, times(1)).changeUserInfo(any(UserEntity.class));
    }

    @Test
    void addUser_Success_ExistingUser() {
        // Given
        UserEntity existingUserEntity = new UserEntity(userId, initialPoints);
        given(userRepository.getUserInfoForUpdate(userId)).willReturn(Optional.of(existingUserEntity));

        // When
        User result = userService.addUserDB(userId, amountToAdd);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals(initialPoints + amountToAdd, result.getPoints());
        verify(userRepository, times(1)).changeUserInfo(any(UserEntity.class));
    }

    @Test
    void updateRechargePoints_Success() {
        // Given
        UserEntity userEntity = new UserEntity(userId, initialPoints);
        given(userRepository.getUserInfo(userId)).willReturn(Optional.of(userEntity));

        // When
        User result = userService.updateRechargePoints(userId, amountToAdd);

        // Then
        assertEquals(initialPoints + amountToAdd, result.getPoints());
        verify(userRepository, times(1)).changeUserInfo(any(UserEntity.class));
    }

    @Test
    void updateUsePoints_Success() {
        // Given
        UserEntity userEntity = new UserEntity(userId, initialPoints);
        given(userRepository.getUserInfo(userId)).willReturn(Optional.of(userEntity));

        // When
        User result = userService.updateUsePoints(userId, amountToUse);

        // Then
        assertEquals(initialPoints - amountToUse, result.getPoints());
        verify(userRepository, times(1)).changeUserInfo(any(UserEntity.class));
    }

    @Test
    void updateUsePoints_Failure_InsufficientPoints() {
        // Given
        UserEntity userEntity = new UserEntity(userId, 50L); // insufficient points
        given(userRepository.getUserInfo(userId)).willReturn(Optional.of(userEntity));

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> userService.updateUsePoints(userId, amountToUse));
        assertEquals("Insufficient points.", exception.getMessage());
        verify(userRepository, never()).changeUserInfo(any(UserEntity.class));
    }

    @Test
    void updateUsePoints_Failure_NegativePoints() {
        // Given
        UserEntity userEntity = new UserEntity(userId, initialPoints);
        given(userRepository.getUserInfo(userId)).willReturn(Optional.of(userEntity));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUsePoints(userId, -amountToUse));
        assertEquals("Points to use must be positive.", exception.getMessage());
        verify(userRepository, never()).changeUserInfo(any(UserEntity.class));
    }

}