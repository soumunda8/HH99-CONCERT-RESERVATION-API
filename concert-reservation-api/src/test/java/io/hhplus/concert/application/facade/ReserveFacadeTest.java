package io.hhplus.concert.application.facade;

import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.domain.concert.Seat;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.domain.repository.concert.SeatRepository;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

public class ReserveFacadeTest {

    private static final String USER_ID = "user1212";

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ConcertService concertService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 좌석 예약 실패 케이스 01 - 좌석 상태 DONE
    @Test
    void shouldThrowExceptionWhenSeatIsAlreadyReserved() {
        // Given
        Long seatNumber = 5L;
        Long concertScheduleId = 12L;
        String userId = USER_ID;

        SeatEntity seatEntity = SeatEntity.builder()
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.DONE.name())
                .build();

        when(seatRepository.chekcSeatNumberStatus(seatNumber)).thenReturn(Optional.of(seatEntity));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.addSeatStatus(seatNumber, concertScheduleId, userId);
        });

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
        verify(seatRepository, never()).save(any(SeatEntity.class));
    }

    // 좌석 예약 실패 케이스 02 - 좌석 없음
    @Test
    void shouldThrowExceptionWhenSeatNotFound() {
        // Given
        Long seatNumber = 5L;
        Long concertScheduleId = 12L;
        String userId = USER_ID;

        when(seatRepository.chekcSeatNumberStatus(seatNumber)).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.addSeatStatus(seatNumber, concertScheduleId, userId);
        });

        assertEquals("좌석을 찾을 수 없습니다.", exception.getMessage());
        verify(seatRepository, never()).save(any(SeatEntity.class));
    }

    // 성공 케이스 - 좌석 상태 예약 성공
    @Test
    void shouldAddSeatStatusSuccessfully() {
        // Given
        Long seatNumber = 5L;
        Long concertScheduleId = 12L;
        String userId = USER_ID;
        SeatEntity seatEntity = SeatEntity.builder()
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.AVAILABLE.name())
                .build();

        when(seatRepository.chekcSeatNumberStatus(seatNumber)).thenReturn(Optional.of(seatEntity));

        SeatEntity uploadedSeatEntity = SeatEntity.builder()
                .concertScheduleId(concertScheduleId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.HELD.name())
                .userId(userId)
                .seatExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();
        when(seatRepository.save(any(SeatEntity.class))).thenReturn(uploadedSeatEntity);

        Seat mappedSeat = new Seat();
        when(modelMapper.map(uploadedSeatEntity, Seat.class)).thenReturn(mappedSeat);

        // When
        Seat seat = concertService.addSeatStatus(seatNumber, concertScheduleId, userId);

        // Then
        assertNotNull(seat);
        verify(seatRepository).save(any(SeatEntity.class));
    }

}