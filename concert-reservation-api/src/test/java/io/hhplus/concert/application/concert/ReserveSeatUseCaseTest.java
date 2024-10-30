package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReserveSeatUseCaseTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReserveSeatUseCase reserveSeatUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 좌석을 찾을 수 없을 때 테스트
    @Test
    void seatNotFound() {
        // Given
        Long seatNumber = 1L;
        Long concertScheduleId = 10L;
        String userId = "user123";

        given(seatRepository.checkSeatNumberStatus(seatNumber)).willReturn(Optional.empty());

        // When, Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);
        });

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
    }

    // 이미 예약된 좌석일 때 테스트
    @Test
    void seatAlreadyReserved() {
        // Given
        Long seatNumber = 1L;
        Long concertScheduleId = 10L;
        String userId = "user123";

        SeatEntity seat = SeatEntity.builder().seatNumber(seatNumber).seatStatus("RESERVED").build();
        given(seatRepository.getSeatNumberAndSeatNumber(concertScheduleId, seatNumber)).willReturn(Optional.ofNullable(seat));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);
        });

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
    }

    // 좌석 예약 성공 테스트
    @Test
    void reserveSeatSuccessfully() {
        // Given
        Long seatNumber = 1L;
        Long concertScheduleId = 10L;
        String userId = "user123";

        SeatEntity seat = SeatEntity.builder().seatNumber(seatNumber).seatStatus("AVAILABLE").build();
        given(seatRepository.getSeatNumberAndSeatNumber(concertScheduleId, seatNumber)).willReturn(Optional.ofNullable(seat));

        // When
        reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);

        // Then
        verify(seatRepository).save(seat);
        assertEquals("RESERVED", seat.getSeatStatus());
        assertEquals(userId, seat.getUserId());
    }
}
