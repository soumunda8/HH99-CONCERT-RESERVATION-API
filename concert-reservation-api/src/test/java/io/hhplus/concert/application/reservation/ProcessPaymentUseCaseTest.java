package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.concert.ConcertScheduleService;
import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.application.concert.SeatService;
import io.hhplus.concert.application.user.UserPointHistoryService;
import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.user.PointActionType;
import io.hhplus.concert.domain.user.User;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.entity.user.UserEntity;
import io.hhplus.concert.infrastructure.mapper.concert.SeatMapper;
import io.hhplus.concert.infrastructure.mapper.reservation.ReservationMapper;
import io.hhplus.concert.infrastructure.mapper.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ProcessPaymentUseCaseTest {

    @Mock
    private ReservationService reservationService;
    @Mock
    private UserService userService;
    @Mock
    private UserPointHistoryService userPointHistoryService;
    @Mock
    private ConcertService concertService;
    @Mock
    private ConcertScheduleService concertScheduleService;
    @Mock
    private SeatService seatService;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

    private Long reservationId = 1L;
    private String userId = "user123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 성공 케이스: 사용자의 포인트가 충분하고 좌석 상태가 HELD일 때
    @Test
    void execute_Success() {
        // Given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(1L)
                .reservationStatus("BOOKED")
                .build();
        Reservation reservation = ReservationMapper.toDomain(reservationEntity);

        SeatEntity seatEntity = SeatEntity.builder()
                .seatId(1L)
                .concertScheduleId(1L)
                .seatStatus(SeatStatus.HELD.name())  // HELD 상태
                .build();
        Seat seat = SeatMapper.toDomain(seatEntity);

        Concert concert = new Concert(1L, "Concert Title", 500L);
        ConcertSchedule concertSchedule = new ConcertSchedule(1L, concert.getConcertId(), null, null, null);

        UserEntity userEntity = UserEntity.builder()
                .userId(userId)
                .points(1000L)
                .build();
        User user = UserMapper.toDomain(userEntity);

        given(reservationService.getReservationInfo(reservationId)).willReturn(reservation);
        given(seatService.getSeatInfo(reservation.getSeatId())).willReturn(seat);
        given(concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId())).willReturn(concertSchedule);
        given(concertService.getConcertInfo(concertSchedule.getConcertId())).willReturn(concert);
        given(userService.getUserInfo(userId)).willReturn(user);

        // When
        Long result = processPaymentUseCase.execute(reservationId, userId);

        // Then
        assertEquals(reservationId, result);
        verify(userService).updateUsePoints(userId, concert.getConcertPrice());
        verify(userPointHistoryService).updatePointsHistory(userId, PointActionType.USE, concert.getConcertPrice());
        verify(reservationService).paidReservationStatus(reservationId);
    }

    // 실패 케이스: 좌석 상태가 HELD가 아닐 때
    @Test
    void execute_Failure_InvalidSeatStatus() {
        // Given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(1L)
                .reservationStatus("BOOKED")
                .build();
        Reservation reservation = ReservationMapper.toDomain(reservationEntity);

        SeatEntity seatEntity = SeatEntity.builder()
                .seatId(1L)
                .concertScheduleId(1L)
                .seatStatus(SeatStatus.DONE.name())  // HELD가 아닌 상태
                .build();
        Seat seat = SeatMapper.toDomain(seatEntity);

        given(reservationService.getReservationInfo(reservationId)).willReturn(reservation);
        given(seatService.getSeatInfo(reservation.getSeatId())).willReturn(seat);

        // When
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> processPaymentUseCase.execute(1L, "user123"));

        // Then
        assertEquals("결제를 진행할 수 없는 좌석 상태입니다.", exception.getMessage());
    }

    // 실패 케이스: 사용자의 포인트가 부족할 때
    @Test
    void execute_Failure_InsufficientPoints() {
        // Given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .seatId(1L)
                .reservationStatus("BOOKED")
                .build();
        Reservation reservation = ReservationMapper.toDomain(reservationEntity);

        SeatEntity seatEntity = SeatEntity.builder()
                .seatId(1L)
                .concertScheduleId(1L)
                .seatStatus(SeatStatus.HELD.name())  // HELD 상태
                .build();
        Seat seat = SeatMapper.toDomain(seatEntity);

        Concert concert = new Concert(1L, "Concert Title", 1500L); // 가격이 사용자의 포인트보다 높음
        ConcertSchedule concertSchedule = new ConcertSchedule(1L, concert.getConcertId(), null, null, null);

        UserEntity userEntity = UserEntity.builder()
                .userId(userId)
                .points(1000L)
                .build();
        User user = UserMapper.toDomain(userEntity);

        given(reservationService.getReservationInfo(reservationId)).willReturn(reservation);
        given(seatService.getSeatInfo(reservation.getSeatId())).willReturn(seat);
        given(concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId())).willReturn(concertSchedule);
        given(concertService.getConcertInfo(concertSchedule.getConcertId())).willReturn(concert);
        given(userService.getUserInfo(userId)).willReturn(user);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> processPaymentUseCase.execute(1L, "user123"));

        // Then
        assertEquals("포인트가 부족합니다.", exception.getMessage());
    }

}