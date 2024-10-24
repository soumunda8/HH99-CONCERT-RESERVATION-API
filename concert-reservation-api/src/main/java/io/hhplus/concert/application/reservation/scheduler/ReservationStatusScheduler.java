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
        logger.info("Starting scheduled task to check and update reservation statuses");

        try {
            reservationService.updateExpiredReservations();
            logger.info("Reservation status update completed successfully");
        } catch (Exception e) {
            logger.error("Error occurred while updating reservation statuses", e);
        }

    }

}