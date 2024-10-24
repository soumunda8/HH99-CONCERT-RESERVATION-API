package io.hhplus.concert.application.reservation;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationRepository;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void updateExpiredReservations() {
        List<ReservationEntity> expiredReservations = reservationRepository.getReservationInfoByStatusBooked(ReservationStatus.BOOKED.toString(), LocalDateTime.now());

        for (ReservationEntity reservation : expiredReservations) {
            canceledReservationStatus(reservation.getReservationId());
        }
    }

    public Reservation getReservationInfo(Long reservationId) {
        ReservationEntity reservationEntity = reservationRepository.getReservationInfo(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        return convertToDomain(reservationEntity);
    }

    public void canceledReservationStatus(Long reservationId) {
        updateReservationStatus(reservationId, ReservationStatus.CANCELED);
    }

    public void paidReservationStatus(Long reservationId) {
        updateReservationStatus(reservationId, ReservationStatus.PAID);
    }

    public void updateReservationStatus(Long reservationId, ReservationStatus reservationStatus) {
        Reservation reservation = getReservationInfo(reservationId);

        ReservationEntity updateReservationEntity = ReservationEntity.builder()
                .reservationId(reservation.getReservationId())
                .reservationStatus(reservationStatus.name())
                .reservationExpireAt(reservation.getReservationExpireAt())
                .seatId(reservation.getSeatId())
                .userId(reservation.getUserId())
                .build();
        reservationRepository.addReservation(updateReservationEntity);
    }

    public Reservation addReservation(Long seatId, String userId) {
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .reservationStatus(ReservationStatus.BOOKED.name())
                .reservationExpireAt(LocalDateTime.now().plusMinutes(10))
                .seatId(seatId)
                .userId(userId)
                .build();
        ReservationEntity reservation = reservationRepository.addReservation(reservationEntity);
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