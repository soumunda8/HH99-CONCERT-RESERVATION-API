package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.domain.concert.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @Mock
    private SeatRepository seatRepository;

    /*@InjectMocks
    private ConcertService concertService;*/

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 콘서트 일정 좌석 조회 실패 케이스 01 - 예약할 콘서트 없음
    /*@Test
    void shouldThrowExceptionWhenConcertScheduleNotFound() {
        // Given
        Long concertScheduleId = 1L;
        given(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).willReturn(Optional.empty());

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableSeats(concertScheduleId);
        });

        assertEquals("해당 콘서트 일정이 존재하지 않습니다.", exception.getMessage());
    }

    // 콘서트 일정 좌석 조회 실패 케이스 02 - 예약 일정 아님
    @Test
    void shouldThrowExceptionWhenReservationDateNotAvailable() {
        // Given
        Long concertScheduleId = 1L;
        ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.builder()
                .concertId(1L)
                .availableReservationDate(LocalDateTime.now().minusDays(1))
                .maxSeatCount(100L)
                .build();

        given(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).willReturn(Optional.of(concertSchedule));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableSeats(concertScheduleId);
        });

        assertEquals("해당 콘서트 예약 가능 날짜가 아닙니다.", exception.getMessage());
    }

    // 콘서트 일정 좌석 조회 실패 케이스 03 - 잔여 좌석 없음
    @Test
    void shouldThrowExceptionWhenNoRemainingSeats() {
        // Given
        Long concertScheduleId = 1L;
        ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.builder()
                .concertId(1L)
                .availableReservationDate(LocalDateTime.now().plusDays(1))
                .maxSeatCount(100L)
                .build();

        List<SeatEntity> reservedSeats = Collections.nCopies(100, new SeatEntity());

        given(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).willReturn(Optional.of(concertSchedule));
        given(seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name())).willReturn(reservedSeats);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableSeats(concertScheduleId);
        });

        assertEquals("해당 콘서트의 잔여 좌석이 없습니다.", exception.getMessage());
    }

    // 성공 케이스
    @Test
    void shouldReturnAvailableSeatsSuccessfully() {
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

        given(concertScheduleRepository.getConcertScheduleInfo(concertScheduleId)).willReturn(Optional.of(concertSchedule));
        given(seatRepository.getSeatNumberAndSeatStatus(concertScheduleId, SeatStatus.DONE.name())).willReturn(reservedSeats);

        // When
        Map<String, Object> result = concertService.getAvailableSeats(concertScheduleId);

        // Then
        assertNotNull(result);
        assertEquals(concertScheduleId, result.get("concertScheduleId"));
        List<Long> reservedSeatNumbers = (List<Long>) result.get("reservedSeatNumbers");
        assertEquals(2, reservedSeatNumbers.size());
        assertTrue(reservedSeatNumbers.contains(1L));
        assertTrue(reservedSeatNumbers.contains(2L));
    }*/

}
