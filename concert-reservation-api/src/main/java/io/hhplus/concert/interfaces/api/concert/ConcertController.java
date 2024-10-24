package io.hhplus.concert.interfaces.api.concert;

import io.hhplus.concert.application.concert.CheckAvailableSeatsUseCase;
import io.hhplus.concert.application.concert.ReserveSeatUseCase;
import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.interfaces.dto.AvailableSeatsResponse;
import io.hhplus.concert.interfaces.dto.ReservationResponse;
import io.hhplus.concert.utils.ValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

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
        ValidationUtils.validateConcertScheduleId(concertScheduleId);

        try {
            AvailableSeatsResponse availableSeats = checkAvailableSeatsUseCase.execute(concertScheduleId);
            return ResponseEntity.ok(availableSeats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 2. 좌석 예약 처리 API
    @PostMapping("/reserve/{concertScheduleId}/{seatNumber}")
    public ResponseEntity<ReservationResponse> reserveSeat(@PathVariable Long concertScheduleId, @PathVariable Long seatNumber, @RequestParam String userId) {
        ValidationUtils.validateConcertScheduleId(concertScheduleId);

        try {
            Reservation reservation = reserveSeatUseCase.execute(seatNumber, concertScheduleId, userId);
            ReservationResponse response = new ReservationResponse(reservation.getReservationId(), "좌석 예약이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}