package io.hhplus.concert.application.reservation.scheduler;

import io.hhplus.concert.application.reservation.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReservationStatusScheduler.class);

    private final ReservationService reservationService;

    public ReservationStatusScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndUpdateReservationStatus() {
        logger.info("Scheduled task: Checking and updating reservation statuses");

        try {
            reservationService.updateExpiredReservations();
            logger.info("Scheduled task completed: Reservation statuses updated successfully");
        } catch (Exception e) {
            logger.error("Scheduled task error: Failed to update reservation statuses", e);
        }
    }

}