package io.hhplus.concert.domain.reservation;

import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<ReservationEntity> getReservationInfo(long reservationId);

    ReservationEntity addReservation(ReservationEntity reservationEntity);

    List<ReservationEntity> getReservationInfoByStatusBooked(String reservationStatusByBooked, LocalDateTime localDateTime);

}