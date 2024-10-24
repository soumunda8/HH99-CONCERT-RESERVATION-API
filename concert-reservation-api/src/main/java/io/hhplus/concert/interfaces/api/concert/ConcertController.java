package io.hhplus.concert.interfaces.api.concert;

import io.hhplus.concert.application.concert.CheckAvailableSeatsUseCase;
import io.hhplus.concert.application.concert.ReserveSeatUseCase;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import io.hhplus.concert.interfaces.dto.ReservationResponse;
import io.hhplus.concert.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    private static final Logger logger = LoggerFactory.getLogger(ConcertController.class);

    private final CheckAvailableSeatsUseCase checkAvailableSeatsUseCase;
    private final ReserveSeatUseCase reserveSeatUseCase;

    public ConcertController(CheckAvailableSeatsUseCase checkAvailableSeatsUseCase,
                             ReserveSeatUseCase reserveSeatUseCase) {
        this.checkAvailableSeatsUseCase = checkAvailableSeatsUseCase;
        this.reserveSeatUseCase = reserveSeatUseCase;
    }

    // 1. 예약 가능한 좌석 정보 조회 API
    @GetMapping("/available/{concertScheduleId}")
    public ResponseEntity<AvailableSeatsResponse> getAvailableSeatsInfo(@PathVariable Long concertScheduleId) {
        logger.info("Received request to get available seats for concertScheduleId: {}", concertScheduleId);

        ValidationUtils.validateConcertScheduleId(concertScheduleId);

        try {
            AvailableSeatsResponse availableSeats = checkAvailableSeatsUseCase.execute(concertScheduleId);
            logger.info("Successfully retrieved available seats for concertScheduleId: {}", concertScheduleId);
            return ResponseEntity.ok(availableSeats);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to retrieve available seats for concertScheduleId: {}, reason: {}", concertScheduleId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 2. 좌석 예약 처리 API
    @PostMapping("/reserve/{concertScheduleId}/{seatNumber}")
    public ResponseEntity<ReservationResponse> reserveSeat(@PathVariable Long concertScheduleId, @PathVariable Long seatNumber, @RequestParam String userId) {
        logger.info("Received request to reserve seatNumber: {} for concertScheduleId: {} by userId: {}", seatNumber, concertScheduleId, userId);

        ValidationUtils.validateConcertScheduleId(concertScheduleId);

        try {
            Reservation reservation = reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);
            ReservationResponse response = new ReservationResponse(reservation.getReservationId(), "좌석 예약이 완료되었습니다.");
            logger.info("Successfully reserved seatNumber: {} for concertScheduleId: {} by userId: {}", seatNumber, concertScheduleId, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to reserve seatNumber: {} for concertScheduleId: {}, reason: {}", seatNumber, concertScheduleId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

}