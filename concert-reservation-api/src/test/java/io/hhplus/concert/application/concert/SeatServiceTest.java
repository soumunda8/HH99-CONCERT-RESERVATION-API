package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    private SeatEntity seatEntity;
    private Long validSeatId = 1L;
    private Long concertScheduleId = 1L;
    private Long seatNumber = 101L;
    private String userId = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        seatEntity = SeatEntity.builder()
                .seatId(validSeatId)
                .concertScheduleId(concertScheduleId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.AVAILABLE.name())
                .build();
    }

    @Test
    void getSeatInfo_Success() {
        when(seatRepository.getSeatInfo(validSeatId)).thenReturn(Optional.of(seatEntity));

        Seat seat = seatService.getSeatInfo(validSeatId);

        assertNotNull(seat);
        assertEquals(validSeatId, seat.getSeatId());
    }

    @Test
    void getSeatInfo_Failure_SeatNotFound() {
        when(seatRepository.getSeatInfo(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> seatService.getSeatInfo(validSeatId));

        assertEquals("예약 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void calculateRemainingSeats_Success() {
        when(seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name()))
                .thenReturn(List.of(seatEntity));

        Long maxSeatCount = 10L;
        Long remainingSeats = seatService.calculateRemainingSeats(concertScheduleId, maxSeatCount);

        assertEquals(9L, remainingSeats);
    }

    @Test
    void calculateRemainingSeats_Failure_NoRemainingSeats() {
        when(seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name()))
                .thenReturn(List.of(seatEntity, seatEntity, seatEntity, seatEntity, seatEntity,
                        seatEntity, seatEntity, seatEntity, seatEntity, seatEntity));

        Long maxSeatCount = 10L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> seatService.calculateRemainingSeats(concertScheduleId, maxSeatCount));

        assertEquals("해당 콘서트의 잔여 좌석이 없습니다.", exception.getMessage());
    }

    @Test
    void getReservedSeatNumbers_Success() {
        when(seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name()))
                .thenReturn(List.of(seatEntity));

        List<Long> reservedSeatNumbers = seatService.getReservedSeatNumbers(concertScheduleId);

        assertNotNull(reservedSeatNumbers);
        assertEquals(1, reservedSeatNumbers.size());
        assertEquals(seatNumber, reservedSeatNumbers.get(0));
    }

    @Test
    void findSeatByNumber_Success() {
        when(seatRepository.checkSeatNumberStatus(seatNumber)).thenReturn(Optional.of(seatEntity));

        Seat seat = seatService.findSeatByNumber(seatNumber);

        assertNotNull(seat);
        assertEquals(seatNumber, seat.getSeatNumber());
    }

    @Test
    void findSeatByNumber_Failure_SeatNotFound() {
        when(seatRepository.checkSeatNumberStatus(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> seatService.findSeatByNumber(seatNumber));

        assertEquals("좌석을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void reserveSeat_Success() {
        when(seatRepository.checkSeatNumberStatus(seatNumber)).thenReturn(Optional.of(seatEntity));
        when(seatRepository.save(any(SeatEntity.class))).thenAnswer(invocation -> {
            SeatEntity savedEntity = invocation.getArgument(0);
            return SeatEntity.builder()
                    .seatId(savedEntity.getSeatId())
                    .concertScheduleId(savedEntity.getConcertScheduleId())
                    .seatNumber(savedEntity.getSeatNumber())
                    .seatStatus(SeatStatus.HELD.name())
                    .userId(savedEntity.getUserId())
                    .seatExpireAt(savedEntity.getSeatExpireAt())
                    .build();
        });

        Seat reservedSeat = seatService.reserveSeat(seatNumber, concertScheduleId, userId);

        assertNotNull(reservedSeat);
        assertEquals(seatNumber, reservedSeat.getSeatNumber());
        assertEquals(SeatStatus.HELD, reservedSeat.getSeatStatus());
    }

    @Test
    void reserveSeat_Failure_SeatAlreadyReserved() {
        // 이미 예약된 좌석으로 설정
        SeatEntity reservedSeatEntity = SeatEntity.builder()
                .seatId(validSeatId)
                .concertScheduleId(concertScheduleId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.DONE.name())  // 상태를 DONE으로 설정
                .userId(userId)
                .build();

        // checkSeatNumberStatus가 예약된 좌석을 반환하도록 Mock 설정
        when(seatRepository.checkSeatNumberStatus(seatNumber)).thenReturn(Optional.of(reservedSeatEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> seatService.reserveSeat(seatNumber, concertScheduleId, userId));

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
    }

}