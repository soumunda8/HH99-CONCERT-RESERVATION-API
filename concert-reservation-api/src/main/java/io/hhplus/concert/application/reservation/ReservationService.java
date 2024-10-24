package io.hhplus.concert.application.reservation;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationRepository;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void updateExpiredReservations() {
        logger.info("Checking for expired reservations to update.");

        List<ReservationEntity> expiredReservations = reservationRepository.getReservationInfoByStatusBooked(ReservationStatus.BOOKED.toString(), LocalDateTime.now());

        for (ReservationEntity reservation : expiredReservations) {
            logger.debug("Updating expired reservation with ID: {}", reservation.getReservationId());
            canceledReservationStatus(reservation.getReservationId());
        }

        logger.info("Expired reservations update process completed.");
    }

    public Reservation getReservationInfo(Long reservationId) {
        logger.info("Fetching reservation info for reservation ID: {}", reservationId);

        ReservationEntity reservationEntity = reservationRepository.getReservationInfo(reservationId)
                .orElseThrow(() -> {
                    logger.error("Reservation with ID {} not found", reservationId);
                    return new IllegalArgumentException("예약 정보를 찾을 수 없습니다.");
                });

        Reservation reservation = convertToDomain(reservationEntity);
        logger.debug("Reservation info retrieved: {}", reservation);

        return reservation;
    }

    public void canceledReservationStatus(Long reservationId) {
        logger.info("Cancelling reservation with ID: {}", reservationId);

        updateReservationStatus(reservationId, ReservationStatus.CANCELED);
        logger.info("Reservation with ID: {} has been cancelled.", reservationId);
    }

    public void paidReservationStatus(Long reservationId) {
        logger.info("Marking reservation as PAID for reservation ID: {}", reservationId);

        updateReservationStatus(reservationId, ReservationStatus.PAID);
        logger.info("Reservation with ID: {} has been marked as PAID.", reservationId);
    }

    public void updateReservationStatus(Long reservationId, ReservationStatus reservationStatus) {
        logger.info("Updating reservation status for reservation ID: {} to {}", reservationId, reservationStatus);

        Reservation reservation = getReservationInfo(reservationId);

        ReservationEntity updateReservationEntity = ReservationEntity.builder()
                .reservationId(reservation.getReservationId())
                .reservationStatus(reservationStatus.name())
                .reservationExpireAt(reservation.getReservationExpireAt())
                .seatId(reservation.getSeatId())
                .userId(reservation.getUserId())
                .build();

        reservationRepository.addReservation(updateReservationEntity);
        logger.info("Reservation status updated successfully for reservation ID: {}", reservationId);
    }

    public Reservation addReservation(Long seatId, String userId) {
        logger.info("Adding new reservation for userId: {}, seatId: {}", userId, seatId);

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .seatId(seatId)
                .userId(userId)
                .build();

        ReservationEntity reservation = reservationRepository.addReservation(reservationEntity);
        logger.info("New reservation added successfully for reservation ID: {}", reservation.getReservationId());

        return convertToDomain(reservation);
    }

    private Reservation convertToDomain(ReservationEntity reservationEntity) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationEntity.getReservationId());
        reservation.setReservationStatus(ReservationStatus.valueOf(reservationEntity.getReservationStatus()));
        reservation.setUserId(reservationEntity.getUserId());
        reservation.setSeatId(reservationEntity.getSeatId());
        reservation.setCreateAt(reservationEntity.getCreateAt());
        return reservation;
    }

}