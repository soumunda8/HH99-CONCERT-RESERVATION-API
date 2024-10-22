package io.hhplus.concert.interfaces.api.concert;

import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.domain.concert.ConcertSchedule;
import io.hhplus.concert.utils.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    // 1. 예약 가능한 날짜 및 잔여 좌석 정보 조회 API
    @GetMapping("/available/{concertScheduleId}")
    public ResponseEntity<Map<String, Object>> getAvailableSeatsInfo(@PathVariable Long concertScheduleId) {
        ValidationUtils.validateConcertScheduleId(concertScheduleId);

        try {
            Map<String, Object> concertSchedule = concertService.getAvailable(concertScheduleId);
            return ResponseEntity.ok(concertSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

    }
}