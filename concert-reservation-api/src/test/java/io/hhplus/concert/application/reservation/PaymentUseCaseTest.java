package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.application.user.UserQueueService;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.mapper.reservation.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

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

    private Long reservationId = 1L;
    private String userId = "user123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 성공 케이스
    @Test
    void execute_Success() {
        // Given
        ReservationEntity reservation = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .reservationStatus(ReservationStatus.BOOKED.name())
                .build();

        willDoNothing().given(checkUserStatusUseCase).execute(userId);
        given(reservationService.getReservationInfo(reservationId)).willReturn(ReservationMapper.toDomain(reservation));

        // When
        paymentUseCase.execute(reservationId, userId);

        // Then
        verify(checkUserStatusUseCase).execute(userId);
        verify(processPaymentUseCase).execute(reservationId, userId);
        verify(userQueueService).removeUserQueueToken(userId);
    }

    // 실패 케이스 01 - 결제 만료된 경우
    @Test
    void execute_Failure_ExpiredPayment() {
        // Given
        ReservationEntity reservation = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .reservationExpireAt(LocalDateTime.now().minusMinutes(5))  // 결제 만료 시간 설정
                .reservationStatus(ReservationStatus.BOOKED.name())  // 필수 필드 설정
                .build();

        given(reservationService.getReservationInfo(reservationId)).willReturn(ReservationMapper.toDomain(reservation));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentUseCase.execute(reservationId, userId));
        assertEquals("결제 시간 초과", exception.getMessage());

        verify(checkUserStatusUseCase, never()).execute(userId);
        verify(processPaymentUseCase, never()).execute(reservationId, userId);
        verify(userQueueService, never()).removeUserQueueToken(userId);
    }

    // 실패 케이스 02 - 사용자 상태가 유효하지 않은 경우
    @Test
    void execute_Failure_InvalidUserStatus() {
        // given
        String userId = "user123";
        Long reservationId = 1L;

        // when
        when(userQueueService.isUserActive(userId)).thenReturn(false);

        // then
        assertThrows(IllegalArgumentException.class, () -> paymentUseCase.execute(reservationId, userId));
    }

}