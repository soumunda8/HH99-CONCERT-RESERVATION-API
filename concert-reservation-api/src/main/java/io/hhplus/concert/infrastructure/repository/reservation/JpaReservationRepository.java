package io.hhplus.concert.infrastructure.repository.reservation;

import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> getByReservationId(long reservationId);

    List<ReservationEntity> findByReservationStatusAndExpireAtBefore(String reservationStatusByBooked, LocalDateTime localDateTime);

}