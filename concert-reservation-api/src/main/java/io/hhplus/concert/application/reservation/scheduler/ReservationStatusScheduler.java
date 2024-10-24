package io.hhplus.concert.application.reservation.scheduler;

import io.hhplus.concert.application.reservation.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationStatusScheduler {

    private final ReservationService reservationService;

    public ReservationStatusScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndUpdateReservationStatus() {
        reservationService.updateExpiredReservations();
    }

}