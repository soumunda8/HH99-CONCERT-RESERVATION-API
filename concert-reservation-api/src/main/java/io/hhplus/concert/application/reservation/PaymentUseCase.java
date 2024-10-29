package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.application.user.UserQueueService;
import io.hhplus.concert.domain.reservation.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PaymentUseCase.class);

    private final CheckUserStatusUseCase checkUserStatusUseCase;
    private final ReservationService reservationService;
    private final UserQueueService userQueueService;
    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentUseCase(CheckUserStatusUseCase checkUserStatusUseCase, ReservationService reservationService, UserQueueService userQueueService, ProcessPaymentUseCase processPaymentUseCase) {
        this.checkUserStatusUseCase = checkUserStatusUseCase;
        this.reservationService = reservationService;
        this.userQueueService = userQueueService;
        this.processPaymentUseCase = processPaymentUseCase;
    }

    public void execute(Long reservationId, String userId) {
        logger.info("Payment process started for reservationId: {}, userId: {}", reservationId, userId);

        try {
            checkUserStatusUseCase.execute(userId);

            Reservation reservation = reservationService.getReservationInfo(reservationId);
            if (reservation.getReservationExpireAt().isBefore(LocalDateTime.now())) {
                logger.warn("Payment expired for reservationId: {}", reservationId);
                throw new IllegalArgumentException("결제 시간 초과");
            }

            processPaymentUseCase.execute(reservationId, userId);
            userQueueService.removeUserQueueToken(userId);
            logger.info("Payment completed and user queue token removed for reservationId: {}, userId: {}", reservationId, userId);

        } catch (Exception ex) {
            logger.error("Error during payment process for reservationId: {}, userId: {}", reservationId, userId, ex);
            throw ex;
        }
    }

}