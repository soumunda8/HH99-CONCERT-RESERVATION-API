package io.hhplus.concert.application.concert;

import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.mapper.concert.ConcertScheduleMapper;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class CheckAvailableSeatsUseCaseTest {

    @Mock
    private SeatService seatService;

    @Mock
    private ConcertScheduleService concertScheduleService;

    @InjectMocks
    private CheckAvailableSeatsUseCase checkAvailableSeatsUseCase;

    private Long concertScheduleId = 1L;
    private ConcertScheduleEntity concertSchedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        concertSchedule = ConcertScheduleEntity.builder()
                .concertScheduleId(concertScheduleId)
                .maxSeatCount(100L)
                .build();
    }

    @Test
    void execute_Success() {
        // Given
        given(concertScheduleService.getConcertScheduleInfo(concertScheduleId)).willReturn(ConcertScheduleMapper.toDomain(concertSchedule));
        willDoNothing().given(concertScheduleService).checkReservationDate(ConcertScheduleMapper.toDomain(concertSchedule));
        given(seatService.calculateRemainingSeats(concertScheduleId, concertSchedule.getMaxSeatCount()))
                .willReturn(90L);
        given(seatService.getReservedSeatNumbers(concertScheduleId))
                .willReturn(List.of(1L, 2L, 3L));

        // When
        AvailableSeatsResponse response = checkAvailableSeatsUseCase.execute(concertScheduleId);

        // Then
        assertNotNull(response);
        assertEquals(90L, response.getRemainingSeats());
        assertEquals(concertScheduleId, response.getConcertScheduleId());
        assertEquals(List.of(1L, 2L, 3L), response.getReservedSeatNumbers());
    }

    @Test
    void execute_Failure_InvalidReservationDate() {
        // Given
        given(concertScheduleService.getConcertScheduleInfo(concertScheduleId)).willReturn(ConcertScheduleMapper.toDomain(concertSchedule));
        willThrow(new IllegalArgumentException("해당 콘서트 예약 가능 날짜가 아닙니다."))
                .given(concertScheduleService).checkReservationDate(ConcertScheduleMapper.toDomain(concertSchedule));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> checkAvailableSeatsUseCase.execute(concertScheduleId));
        assertEquals("해당 콘서트 예약 가능 날짜가 아닙니다.", exception.getMessage());
    }

    @Test
    void execute_Failure_NoRemainingSeats() {
        // Given
        given(concertScheduleService.getConcertScheduleInfo(concertScheduleId)).willReturn(ConcertScheduleMapper.toDomain(concertSchedule));
        willDoNothing().given(concertScheduleService).checkReservationDate(ConcertScheduleMapper.toDomain(concertSchedule));
        willThrow(new IllegalArgumentException("해당 콘서트의 잔여 좌석이 없습니다."))
                .given(seatService).calculateRemainingSeats(concertScheduleId, concertSchedule.getMaxSeatCount());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> checkAvailableSeatsUseCase.execute(concertScheduleId));
        assertEquals("해당 콘서트의 잔여 좌석이 없습니다.", exception.getMessage());
    }

}