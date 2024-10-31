package io.hhplus.concert.application.reservation;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationRepository;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import io.hhplus.concert.infrastructure.mapper.reservation.ReservationMapper;
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
        logger.info("Starting process to update expired reservations.");

        List<ReservationEntity> expiredReservations = reservationRepository.getReservationInfoByStatusBooked(ReservationStatus.BOOKED.name(), LocalDateTime.now());

        expiredReservations.forEach(reservation -> canceledReservationStatus(reservation.getReservationId()));

        logger.info("Expired reservations update process completed.");
    }

    public Reservation getReservationInfo(Long reservationId) {
        return reservationRepository.getReservationInfo(reservationId)
                .map(ReservationMapper::toDomain)
                .orElseThrow(() -> {
                    logger.error("Reservation with ID {} not found", reservationId);
                    return new IllegalArgumentException("예약 정보를 찾을 수 없습니다.");
                });
    }

    public void canceledReservationStatus(Long reservationId) {
        logger.info("Cancelling reservation with ID: {}", reservationId);
        updateReservationStatus(reservationId, ReservationStatus.CANCELED);
    }

    public void paidReservationStatus(Long reservationId) {
        logger.info("Marking reservation as PAID for reservation ID: {}", reservationId);
        updateReservationStatus(reservationId, ReservationStatus.PAID);
    }

    public void updateReservationStatus(Long reservationId, ReservationStatus reservationStatus) {
        Reservation reservation = getReservationInfo(reservationId);
        reservation.setReservationStatus(reservationStatus);

        ReservationEntity updateReservationEntity = ReservationMapper.toEntity(reservation);
        reservationRepository.addReservation(updateReservationEntity);

        logger.info("Reservation status updated to {} for reservation ID: {}", reservationStatus, reservationId);
    }

    public Reservation addNewReservation(Long seatId, String userId) {
        Reservation reservation = new Reservation(null, userId, seatId, ReservationStatus.BOOKED, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        ReservationEntity savedReservation = reservationRepository.addReservation(ReservationMapper.toEntity(reservation));
        logger.info("New reservation added with ID: {}", savedReservation.getReservationId());

        return ReservationMapper.toDomain(savedReservation);
    }

}