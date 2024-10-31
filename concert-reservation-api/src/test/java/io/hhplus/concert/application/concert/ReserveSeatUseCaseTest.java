package io.hhplus.concert.application.concert;

import io.hhplus.concert.application.reservation.ReservationService;
import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.mapper.concert.SeatMapper;
import io.hhplus.concert.infrastructure.mapper.reservation.ReservationMapper;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class ReserveSeatUseCaseTest {

    @Mock
    private SeatService seatService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private CheckUserStatusUseCase checkUserStatusUseCase;

    @Mock
    private CheckAvailableSeatsUseCase checkAvailableSeatsUseCase;

    @InjectMocks
    private ReserveSeatUseCase reserveSeatUseCase;

    private Long concertScheduleId = 1L;
    private Long seatNumber = 101L;
    private String userId = "testUser";
    private SeatEntity seat;
    private ReservationEntity reservation;
    private AvailableSeatsResponse availableSeatsResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // SeatEntity 생성
        seat = SeatEntity.builder()
                .seatId(1L)
                .concertScheduleId(concertScheduleId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.AVAILABLE.name())  // SeatStatus를 AVAILABLE로 설정
                .build();

        // ReservationEntity 생성 및 reservationStatus 설정
        reservation = ReservationEntity.builder()
                .reservationId(1L)
                .userId(userId)
                .seatId(seat.getSeatId())
                .reservationStatus(ReservationStatus.BOOKED.name())  // reservationStatus를 PENDING으로 설정
                .build();

        availableSeatsResponse = new AvailableSeatsResponse();
        availableSeatsResponse.setConcertScheduleId(concertScheduleId);
        availableSeatsResponse.setRemainingSeats(90L);
        availableSeatsResponse.setReservedSeatNumbers(List.of(102L, 103L));
    }

    @Test
    void execute_Success() {
        // Given
        willDoNothing().given(checkUserStatusUseCase).execute(userId);
        given(checkAvailableSeatsUseCase.execute(concertScheduleId)).willReturn(availableSeatsResponse);

        // SeatMapper와 ReservationMapper를 사용하여 도메인 객체 반환
        given(seatService.reserveSeat(seatNumber, concertScheduleId, userId)).willReturn(SeatMapper.toDomain(seat));
        given(reservationService.addNewReservation(seat.getSeatId(), userId)).willReturn(ReservationMapper.toDomain(reservation));

        // When
        Reservation result = reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);

        // Then
        assertNotNull(result);
        assertEquals(reservation.getReservationId(), result.getReservationId());
        assertEquals(userId, result.getUserId());
        assertEquals(seat.getSeatId(), result.getSeatId());
    }

    @Test
    void execute_Failure_UserStatusInvalid() {
        // Given
        willThrow(new IllegalArgumentException("사용자 상태가 유효하지 않습니다.")).given(checkUserStatusUseCase).execute(userId);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId));

        assertEquals("사용자 상태가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    void execute_Failure_NoAvailableSeats() {
        // Given
        given(checkAvailableSeatsUseCase.execute(concertScheduleId)).willReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId));

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
    }

    @Test
    void execute_Failure_SeatAlreadyReserved() {
        // Given
        willDoNothing().given(checkUserStatusUseCase).execute(userId);
        given(checkAvailableSeatsUseCase.execute(concertScheduleId)).willReturn(availableSeatsResponse);
        willThrow(new IllegalArgumentException("이미 예약된 좌석입니다."))
                .given(seatService).reserveSeat(seatNumber, concertScheduleId, userId);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId));

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
    }

}