package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.concert.Concert;
import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService concertService;

    private ConcertEntity concertEntity;
    private Long concertId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        concertEntity = ConcertEntity.builder()
                .concertId(concertId)
                .concertTitle("Test Concert")
                .concertPrice(1000L)
                .build();
    }

    @Test
    void getConcertInfo_Success() {
        // Given
        given(concertRepository.getConcertInfo(concertId)).willReturn(Optional.of(concertEntity));

        // When
        Concert concert = concertService.getConcertInfo(concertId);

        // Then
        assertNotNull(concert);
        assertEquals(concertId, concert.getConcertId());
        assertEquals("Test Concert", concert.getConcertTitle());
        assertEquals(1000L, concert.getConcertPrice());
    }

    @Test
    void getConcertInfo_Failure_ConcertNotFound() {
        // Given
        given(concertRepository.getConcertInfo(concertId)).willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> concertService.getConcertInfo(concertId));

        assertEquals("해당 콘서트가 존재하지 않습니다.", exception.getMessage());
    }

}
