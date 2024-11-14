package io.hhplus.concert.application.reservation;

import io.hhplus.concert.application.user.CheckUserStatusUseCase;
import io.hhplus.concert.application.user.UserQueueService;
import io.hhplus.concert.domain.concert.PaymentCompletedEvent;
import io.hhplus.concert.domain.reservation.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PaymentUseCase.class);

    private final CheckUserStatusUseCase checkUserStatusUseCase;
    private final ReservationService reservationService;
    private final UserQueueService userQueueService;
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentUseCase(CheckUserStatusUseCase checkUserStatusUseCase, ReservationService reservationService, UserQueueService userQueueService, ProcessPaymentUseCase processPaymentUseCase, ApplicationEventPublisher eventPublisher) {
        this.checkUserStatusUseCase = checkUserStatusUseCase;
        this.reservationService = reservationService;
        this.userQueueService = userQueueService;
        this.processPaymentUseCase = processPaymentUseCase;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(Long reservationId, String userId) {
        logger.info("Payment process started for reservationId: {}, userId: {}", reservationId, userId);

        try {
            // 결제 시간 초과 여부 먼저 확인
            Reservation reservation = reservationService.getReservationInfo(reservationId);

            if (reservation == null) {
                logger.error("Reservation not found for reservationId: {}", reservationId);
                throw new IllegalArgumentException("예약 정보를 찾을 수 없습니다.");
            }

            if (reservation.getReservationExpireAt().isBefore(LocalDateTime.now())) {
                logger.warn("Payment expired for reservationId: {}", reservationId);
                throw new IllegalArgumentException("결제 시간 초과");
            }

            // 결제 만료가 아닌 경우에만 사용자 상태 확인
            checkUserStatusUseCase.execute(userId);

            // 결제 처리
            processPaymentUseCase.execute(reservationId, userId);

            // 큐 제거 : event listener
            eventPublisher.publishEvent(new PaymentCompletedEvent(this, userId));

            logger.info("Payment completed and user queue token removed for reservationId: {}, userId: {}", reservationId, userId);

        } catch (Exception ex) {
            logger.error("Error during payment process for reservationId: {}, userId: {}", reservationId, userId, ex);
            throw ex;
        }

    }

}