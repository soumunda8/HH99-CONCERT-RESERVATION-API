package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.application.user.UserQueueService;
import io.hhplus.concert.domain.reservation.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentUseCase {

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
        checkUserStatusUseCase.execute(userId);

        Reservation reservation = reservationService.getReservationInfo(reservationId);
        if (reservation.getReservationExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("결제 시간 초과");
        }

        processPaymentUseCase.execute(reservationId, userId);

        userQueueService.removeUserQueueToken(userId);
    }
}