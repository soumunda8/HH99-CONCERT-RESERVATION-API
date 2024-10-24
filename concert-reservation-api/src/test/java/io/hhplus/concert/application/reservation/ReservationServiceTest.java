package io.hhplus.concert.application.reservation;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationRepository;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // 실패 케이스 01 - 예약 정보가 없을 때 예외 발생
    @Test
    void testGetReservationInfo_notFound() {
        Long reservationId = 1L;

        when(reservationRepository.getReservationInfo(reservationId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.getReservationInfo(reservationId);
        });

        assertEquals("예약 정보를 찾을 수 없습니다.", exception.getMessage());
        verify(reservationRepository, times(1)).getReservationInfo(reservationId);
    }

    // 성공 케이스 예약 정보 조회 성공
    @Test
    void successGetReservationInfo() {
        Long reservationId = 1L;
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .seatId(100L)
                .userId("user123")
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(reservationRepository.getReservationInfo(reservationId)).thenReturn(Optional.of(reservationEntity));

        Reservation reservation = reservationService.getReservationInfo(reservationId);

        assertNotNull(reservation);
        assertEquals(reservationId, reservation.getReservationId());
        assertEquals(ReservationStatus.BOOKED, reservation.getReservationStatus());
        assertEquals("user123", reservation.getUserId());
        verify(reservationRepository, times(1)).getReservationInfo(reservationId);
    }

    // 성공 케이스 - 예약 상태가 만료된 예약들을 업데이트
    @Test
    void successUpdateExpiredReservations_() {
        Long reservationId = 1L;

        // Given
        List<ReservationEntity> expiredReservations = List.of(
                ReservationEntity.builder()
                        .reservationId(reservationId)
                        .reservationStatus(ReservationStatus.BOOKED.name())
                        .seatId(100L)
                        .userId("user123")
                        .reservationExpireAt(LocalDateTime.now().minusMinutes(5))
                        .build()
        );

        when(reservationRepository.getReservationInfoByStatusBooked(anyString(), any(LocalDateTime.class)))
                .thenReturn(expiredReservations);

        when(reservationRepository.getReservationInfo(reservationId)).thenReturn(Optional.of(expiredReservations.get(0)));

        // When
        reservationService.updateExpiredReservations();

        // Then
        verify(reservationRepository, times(1)).getReservationInfoByStatusBooked(anyString(), any(LocalDateTime.class));
        verify(reservationRepository, times(1)).addReservation(any(ReservationEntity.class)); // 상태가 CANCELED로 업데이트되었는지 확인
    }

    // 성공 케이스 - 예약 상태를 취소로 변경
    @Test
    void successCanceledReservationStatus() {
        Long reservationId = 1L;

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .seatId(100L)
                .userId("user123")
                .build();

        when(reservationRepository.getReservationInfo(reservationId)).thenReturn(Optional.of(reservationEntity));

        reservationService.canceledReservationStatus(reservationId);

        verify(reservationRepository, times(1)).addReservation(any(ReservationEntity.class)); // 상태가 CANCELED로 업데이트되었는지 확인
    }

    // 성공 케이스 - 예약 추가
    @Test
    void successAddReservation() {
        Long seatId = 100L;
        String userId = "user123";
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(1L)
                .seatId(seatId)
                .userId(userId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(reservationRepository.addReservation(any(ReservationEntity.class))).thenReturn(reservationEntity);

        Reservation reservation = reservationService.addReservation(seatId, userId);

        assertNotNull(reservation);
        assertEquals(1L, reservation.getReservationId());
        assertEquals(seatId, reservation.getSeatId());
        assertEquals(userId, reservation.getUserId());
        assertEquals(ReservationStatus.BOOKED, reservation.getReservationStatus());
        verify(reservationRepository, times(1)).addReservation(any(ReservationEntity.class));
    }
}