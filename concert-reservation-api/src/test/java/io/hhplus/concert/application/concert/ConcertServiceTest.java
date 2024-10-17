package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.domain.repository.concert.ConcertRepository;
import io.hhplus.concert.domain.repository.concert.ConcertScheduleRepository;
import io.hhplus.concert.domain.repository.concert.SeatRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ConcertService concertService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 콘서트 일정 좌석 조회 실패 케이스 01 - 예약할 콘서트 없음
    @Test
    void concertScheduleNotFound() {
        // Given
        Long concertScheduleId = 1L;

        when(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailable(concertScheduleId);
        });

        assertEquals("해당 콘서트 일정이 존재하지 않습니다.", exception.getMessage());
    }

    // 콘서트 일정 좌석 조회 실패 케이스 02 - 예약 일정 아님
    @Test
    void reservationDateNotAvailable() {
        // Given
        Long concertScheduleId = 1L;
        ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.builder()
                .concertId(1L)
                .availableReservationDate(LocalDateTime.now().minusDays(1))
                .maxSeatCount(100L)
                .build();

        when(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).thenReturn(Optional.of(concertSchedule));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailable(concertScheduleId);
        });

        assertEquals("해당 콘서트 예약 가능 날짜가 아닙니다.", exception.getMessage());
    }

    // 콘서트 일정 좌석 조회 실패 케이스 03 - 잔여 좌석 없음
    @Test
    void noRemainingSeats() {
        // Given
        Long concertScheduleId = 1L;
        ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.builder()
                .concertId(1L)
                .availableReservationDate(LocalDateTime.now().plusDays(1))
                .maxSeatCount(100L)
                .build();

        List<SeatEntity> reservedSeats = Collections.nCopies(100, new SeatEntity());

        when(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).thenReturn(Optional.of(concertSchedule));
        when(seatRepository.getSeatStatusDONE(concertScheduleId, SeatStatus.DONE.name())).thenReturn(reservedSeats);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailable(concertScheduleId);
        });

        assertEquals("해당 콘서트의 잔여 좌석이 없습니다.", exception.getMessage());
    }

    // 성공 케이스
    @Test
    void successConcertSchedule() {
        // Given
        Long concertScheduleId = 1L;
        ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.builder()
                .concertId(1L)
                .availableReservationDate(LocalDateTime.now().plusDays(1))
                .maxSeatCount(80L)
                .build();

        SeatEntity seatEntity1 = SeatEntity.builder().seatNumber(1L).build();
        SeatEntity seatEntity2 = SeatEntity.builder().seatNumber(2L).build();

        List<SeatEntity> reservedSeats = Arrays.asList(seatEntity1, seatEntity2);

        when(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).thenReturn(Optional.of(concertSchedule));
        when(seatRepository.getSeatStatusDONE(concertScheduleId, SeatStatus.DONE.name())).thenReturn(reservedSeats);

        // When
        Map<String, Object> result = concertService.getAvailable(concertScheduleId);

        // Then
        assertNotNull(result);
        assertEquals(concertScheduleId, result.get("concertScheduleId"));
        List<Long> reservedSeatNumbers = (List<Long>) result.get("reservedSeatNumbers");
        assertEquals(2, reservedSeatNumbers.size());
        assertTrue(reservedSeatNumbers.contains(1L));
        assertTrue(reservedSeatNumbers.contains(2L));
    }

}