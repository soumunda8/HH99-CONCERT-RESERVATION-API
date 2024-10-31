package io.hhplus.concert.integration.concert;

import io.hhplus.concert.application.concert.CheckAvailableSeatsUseCase;
import io.hhplus.concert.application.concert.ConcertScheduleService;
import io.hhplus.concert.application.concert.SeatService;
import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.domain.concert.SeatRepository;
import io.hhplus.concert.domain.concert.SeatStatus;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import io.hhplus.concert.infrastructure.entity.concert.SeatEntity;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CheckAvailableSeatsUseCaseIntegrationTest {

    @Autowired
    private CheckAvailableSeatsUseCase checkAvailableSeatsUseCase;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ConcertRepository concertRepository;

    private Long concertScheduleId;
    private Long concertId;

    @BeforeEach
    void setUp() {
        // 공연 생성 후 concertId 설정
        ConcertEntity concertEntity = ConcertEntity.builder()
                .concertTitle("Test Concert")
                .concertPrice(1000L)
                .build();
        concertRepository.save(concertEntity);
        concertId = concertEntity.getConcertId();

        // 공연 일정 및 좌석 데이터 초기화 설정
        concertScheduleId = 1L;
        ConcertScheduleEntity concertScheduleEntity = ConcertScheduleEntity.builder()
                .concertScheduleId(concertScheduleId)
                .concertId(concertId)  // concertId 설정
                .maxSeatCount(100L)
                .remainingSeatCount(0L)
                .availableReservationDate(LocalDateTime.now().plusDays(1))
                .build();
        concertScheduleRepository.save(concertScheduleEntity);

        SeatEntity seatEntity = SeatEntity.builder()
                .concertScheduleId(concertScheduleId)
                .seatNumber(1L)
                .seatStatus(SeatStatus.DONE.name())
                .build();
        seatRepository.save(seatEntity);
    }

    @Test
    void checkAvailableSeats_Success() {
        // 예약 가능 좌석 확인 통합 테스트
        AvailableSeatsResponse response = checkAvailableSeatsUseCase.execute(concertScheduleId);

        assertNotNull(response);
        assertEquals(99L, response.getRemainingSeats()); // 총 좌석 100 중 예약 완료된 좌석 1
        assertEquals(List.of(1L), response.getReservedSeatNumbers());
    }

    @Test
    void checkAvailableSeats_Failure_InvalidDate() {
        ConcertScheduleEntity invalidSchedule = ConcertScheduleEntity.builder()
                .concertScheduleId(2L)
                .concertId(concertId)  // concertId 설정
                .maxSeatCount(100L)
                .remainingSeatCount(0L)
                .availableReservationDate(LocalDateTime.now().minusDays(1)) // 예약 불가능 날짜
                .build();
        concertScheduleRepository.save(invalidSchedule);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> checkAvailableSeatsUseCase.execute(invalidSchedule.getConcertScheduleId()));

        assertEquals("해당 콘서트 예약 가능 날짜가 아닙니다.", exception.getMessage());
    }

}