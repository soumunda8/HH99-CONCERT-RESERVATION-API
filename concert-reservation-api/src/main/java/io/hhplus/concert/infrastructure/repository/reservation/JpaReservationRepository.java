package io.hhplus.concert.infrastructure.repository.reservation;

import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, Long> {
}