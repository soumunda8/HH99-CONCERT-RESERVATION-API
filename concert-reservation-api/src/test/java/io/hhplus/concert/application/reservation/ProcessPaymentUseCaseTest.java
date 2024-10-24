package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.concert.ConcertScheduleService;
import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.application.concert.SeatService;
import io.hhplus.concert.application.user.UserPointHistoryService;
import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    private SeatService seatService;

    @Mock
    private ConcertScheduleService concertScheduleService;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

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
        when(reservation.getSeatId()).thenReturn(1L);
        when(reservationService.getReservationInfo(reservationId)).thenReturn(reservation);

        Seat seat = mock(Seat.class);
        when(seat.getConcertScheduleId()).thenReturn(1L);
        when(seatService.getSeatInfo(reservation.getSeatId())).thenReturn(seat);

        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        when(concertSchedule.getConcertId()).thenReturn(1L);
        when(concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId())).thenReturn(concertSchedule);

        Concert concert = mock(Concert.class);
        when(concert.getConcertPrice()).thenReturn(50000L);
        when(concertService.getConcertInfo(concertSchedule.getConcertId())).thenReturn(concert);

        User user = mock(User.class);
        when(user.getPoints()).thenReturn(100000L);
        when(userService.getUserInfo(userId)).thenReturn(user);

        // When
        Long result = processPaymentUseCase.execute(reservationId, userId);

        // Then
        verify(userService, times(1)).updateUsePoints(userId, 50000L);
        verify(userPointHistoryService, times(1)).updateUsePointsHistory(userId, 50000L);
        verify(reservationService, times(1)).paidReservationStatus(reservationId);

        assertEquals(reservationId, result);
    }

    // 실패 케이스 01 - 사용자 포인트가 부족할 때 예외 발생
    @Test
    void failInsufficientPoints() {
        Long reservationId = 1L;
        String userId = "user123";

        // Given
        Reservation reservation = mock(Reservation.class);
        when(reservation.getSeatId()).thenReturn(1L);
        when(reservationService.getReservationInfo(reservationId)).thenReturn(reservation);

        Seat seat = mock(Seat.class);
        when(seat.getConcertScheduleId()).thenReturn(1L);
        when(seatService.getSeatInfo(reservation.getSeatId())).thenReturn(seat);

        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        when(concertSchedule.getConcertId()).thenReturn(1L);
        when(concertScheduleService.getConcertScheduleInfo(seat.getConcertScheduleId())).thenReturn(concertSchedule);

        Concert concert = mock(Concert.class);
        when(concert.getConcertPrice()).thenReturn(50000L);
        when(concertService.getConcertInfo(concertSchedule.getConcertId())).thenReturn(concert);

        User user = mock(User.class);
        when(user.getPoints()).thenReturn(30000L);
        when(userService.getUserInfo(userId)).thenReturn(user);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processPaymentUseCase.execute(reservationId, userId);
        });

        assertEquals("포인트가 부족합니다.", exception.getMessage());

        verify(userService, never()).updateUsePoints(userId, anyLong());
        verify(userPointHistoryService, never()).updateUsePointsHistory(userId, anyLong());
        verify(reservationService, never()).paidReservationStatus(reservationId);
    }
}