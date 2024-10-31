package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.mapper.concert.ConcertScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.when;

public class ConcertScheduleServiceTest {

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @InjectMocks
    private ConcertScheduleService concertScheduleService;

    private ConcertScheduleEntity validConcertSchedule;
    private Long validConcertScheduleId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Manually initialize mocks
        validConcertScheduleId = 1L;
        validConcertSchedule = ConcertScheduleEntity.builder()
                .concertScheduleId(validConcertScheduleId)
                .availableReservationDate(LocalDateTime.now().plusDays(1)) // Date in the future
                .build();
    }

    @Test
    void getConcertScheduleInfo_Success() {
        when(concertScheduleRepository.getConcertScheduleInfo(validConcertScheduleId))
                .thenReturn(Optional.ofNullable(validConcertSchedule));

        ConcertSchedule result = concertScheduleService.getConcertScheduleInfo(validConcertScheduleId);

        assertNotNull(result);
        assertEquals(validConcertScheduleId, result.getConcertScheduleId());
    }

    @Test
    void getConcertScheduleInfo_Failure_NotFound() {
        when(concertScheduleRepository.getConcertScheduleInfo(anyLong()))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> concertScheduleService.getConcertScheduleInfo(validConcertScheduleId));

        assertEquals("해당 콘서트 일정이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void checkReservationDate_Success() {
        assertDoesNotThrow(() -> concertScheduleService.checkReservationDate(ConcertScheduleMapper.toDomain(validConcertSchedule)));
    }

    @Test
    void checkReservationDate_Failure_InvalidDate() {
        ConcertScheduleEntity pastDateConcertSchedule = ConcertScheduleEntity.builder()
                .concertScheduleId(2L)
                .availableReservationDate(LocalDateTime.now().minusDays(1)) // Date in the past
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> concertScheduleService.checkReservationDate(ConcertScheduleMapper.toDomain(pastDateConcertSchedule)));

        assertEquals("해당 콘서트 예약 가능 날짜가 아닙니다.", exception.getMessage());
    }

}