package io.hhplus.concert.application.reservation.scheduler;

import io.hhplus.concert.application.reservation.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReservationStatusSchedulerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationStatusScheduler reservationStatusScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // 성공 케이스
    @Test
    void successCheckAndUpdateReservationStatus() {
        // When
        reservationStatusScheduler.checkAndUpdateReservationStatus();

        // Then
        verify(reservationService, times(1)).updateExpiredReservations();
    }

    // 실패 케이스 01 - 예약 만료 업데이트 중 예외 발생
    @Test
    void testCheckAndUpdateReservationStatus_failure() {
        // Given
        doThrow(new RuntimeException("예약 만료 업데이트 실패")).when(reservationService).updateExpiredReservations();

        // When
        try {
            reservationStatusScheduler.checkAndUpdateReservationStatus();
        } catch (Exception e) {
            // Then
            assertEquals("예약 만료 업데이트 실패", e.getMessage());
        }

        verify(reservationService, times(1)).updateExpiredReservations();
    }

}