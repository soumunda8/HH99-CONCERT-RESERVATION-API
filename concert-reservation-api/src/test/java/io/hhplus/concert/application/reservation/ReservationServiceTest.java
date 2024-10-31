package io.hhplus.concert.application.reservation;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationRepository;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.mapper.reservation.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Long reservationId = 1L;
    private Long seatId = 100L;
    private String userId = "user123";
    private ReservationEntity reservationEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    @Test
    void updateExpiredReservations_Success() {
        // Given
        ReservationEntity expiredReservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().minusMinutes(1)) // 만료된 시간 설정
                .build();

        given(reservationRepository.getReservationInfoByStatusBooked(eq(ReservationStatus.BOOKED.name()), any(LocalDateTime.class)))
                .willReturn(List.of(expiredReservationEntity));
        given(reservationRepository.getReservationInfo(reservationId)).willReturn(Optional.of(expiredReservationEntity));

        // When
        reservationService.updateExpiredReservations();

        // Then
        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(reservationRepository, times(1)).addReservation(captor.capture());

        ReservationEntity updatedEntity = captor.getValue();
        assertEquals(ReservationStatus.CANCELED.name(), updatedEntity.getReservationStatus());
    }

    @Test
    void getReservationInfo_Success() {
        // Given
        given(reservationRepository.getReservationInfo(reservationId)).willReturn(Optional.of(reservationEntity));

        // When
        Reservation reservation = reservationService.getReservationInfo(reservationId);

        // Then
        assertNotNull(reservation);
        assertEquals(reservationId, reservation.getReservationId());
        assertEquals(userId, reservation.getUserId());
        assertEquals(ReservationStatus.BOOKED, reservation.getReservationStatus());
    }

    @Test
    void getReservationInfo_Failure_NotFound() {
        // Given
        given(reservationRepository.getReservationInfo(reservationId)).willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.getReservationInfo(reservationId));
        assertEquals("예약 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void canceledReservationStatus_Success() {
        // Given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();
        given(reservationRepository.getReservationInfo(reservationId)).willReturn(Optional.of(reservationEntity));

        // When
        reservationService.canceledReservationStatus(reservationId);

        // Then
        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(reservationRepository, times(1)).addReservation(captor.capture());

        ReservationEntity updatedEntity = captor.getValue();
        assertEquals(ReservationStatus.CANCELED.name(), updatedEntity.getReservationStatus());
    }

    @Test
    void paidReservationStatus_Success() {
        // Given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();
        given(reservationRepository.getReservationInfo(reservationId)).willReturn(Optional.of(reservationEntity));

        // When
        reservationService.paidReservationStatus(reservationId);

        // Then
        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(reservationRepository, times(1)).addReservation(captor.capture());

        ReservationEntity updatedEntity = captor.getValue();
        assertEquals(ReservationStatus.PAID.name(), updatedEntity.getReservationStatus());
    }

    @Test
    void updateReservationStatus_Success() {
        // Given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();
        given(reservationRepository.getReservationInfo(reservationId)).willReturn(Optional.of(reservationEntity));

        // When
        reservationService.updateReservationStatus(reservationId, ReservationStatus.CANCELED);

        // Then
        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(reservationRepository, times(1)).addReservation(captor.capture());

        ReservationEntity updatedEntity = captor.getValue();
        assertEquals(ReservationStatus.CANCELED.name(), updatedEntity.getReservationStatus());
    }

    @Test
    void addNewReservation_Success() {
        // Given
        Reservation newReservation = new Reservation(null, userId, seatId, ReservationStatus.BOOKED, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        ReservationEntity newReservationEntity = ReservationMapper.toEntity(newReservation);

        given(reservationRepository.addReservation(any(ReservationEntity.class))).willReturn(newReservationEntity);

        // When
        Reservation result = reservationService.addNewReservation(seatId, userId);

        // Then
        assertNotNull(result);
        assertEquals(seatId, result.getSeatId());
        assertEquals(userId, result.getUserId());
        assertEquals(ReservationStatus.BOOKED, result.getReservationStatus());
    }

}