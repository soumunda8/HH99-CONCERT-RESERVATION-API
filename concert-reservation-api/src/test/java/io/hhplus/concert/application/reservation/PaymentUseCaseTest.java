package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.application.user.UserQueueService;
import io.hhplus.concert.domain.reservation.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PaymentUseCaseTest {

    @Mock
    private CheckUserStatusUseCase checkUserStatusUseCase;

    @Mock
    private ReservationService reservationService;

    @Mock
    private UserQueueService userQueueService;

    @Mock
    private ProcessPaymentUseCase processPaymentUseCase;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // 성공 케이스
    @Test
    void successExecute() {
        Long reservationId = 1L;
        String userId = "user123";

        // Given
        Reservation reservation = mock(Reservation.class);
        when(reservationService.getReservationInfo(reservationId)).thenReturn(reservation);
        when(reservation.getReservationExpireAt()).thenReturn(LocalDateTime.now().plusMinutes(10));

        // When
        paymentUseCase.execute(reservationId, userId);

        // Then
        verify(checkUserStatusUseCase, times(1)).execute(userId);
        verify(processPaymentUseCase, times(1)).execute(reservationId, userId);
        verify(userQueueService, times(1)).removeUserQueueToken(userId);
    }

    // 실패 케이스 01 - 결제 시간 초과
    @Test
    void failPaymentTimeout() {
        Long reservationId = 1L;
        String userId = "user123";

        // Given
        Reservation reservation = mock(Reservation.class);
        when(reservationService.getReservationInfo(reservationId)).thenReturn(reservation);
        when(reservation.getReservationExpireAt()).thenReturn(LocalDateTime.now().minusMinutes(5));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentUseCase.execute(reservationId, userId), "결제 시간 초과");

        verify(checkUserStatusUseCase, times(1)).execute(userId);
        verify(processPaymentUseCase, never()).execute(reservationId, userId);
        verify(userQueueService, never()).removeUserQueueToken(userId);
    }

    // 실패 케이스 02 - 유저 상태가 유효하지 않은 경우
    @Test
    void failInvalidUserStatus() {
        Long reservationId = 1L;
        String userId = "user123";

        // Given - 유저 상태 체크에서 예외 발생 설정
        doThrow(new IllegalStateException("유저 상태가 유효하지 않습니다")).when(checkUserStatusUseCase).execute(userId);

        // When & Then
        assertThrows(IllegalStateException.class, () -> paymentUseCase.execute(reservationId, userId), "유저 상태가 유효하지 않습니다");

        verify(checkUserStatusUseCase, times(1)).execute(userId);
        verify(reservationService, never()).getReservationInfo(reservationId);
        verify(processPaymentUseCase, never()).execute(reservationId, userId);
        verify(userQueueService, never()).removeUserQueueToken(userId);
    }

}