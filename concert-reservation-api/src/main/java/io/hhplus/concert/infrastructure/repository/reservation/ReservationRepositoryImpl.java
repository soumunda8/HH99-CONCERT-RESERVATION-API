package io.hhplus.concert.infrastructure.repository.reservation;

import io.hhplus.concert.domain.reservation.ReservationRepository;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    public final JpaReservationRepository jpaReservationRepository;

    public ReservationRepositoryImpl(JpaReservationRepository jpaReservationRepository) {
        this.jpaReservationRepository = jpaReservationRepository;
    }

    @Override
    public Optional<ReservationEntity> getReservationInfo(long reservationId) {
        return jpaReservationRepository.getByReservationId(reservationId);
    }

    @Override
    public List<ReservationEntity> getReservationInfoByStatusBooked(String reservationStatusByBooked, LocalDateTime localDateTime) {
        return jpaReservationRepository.findByReservationStatusAndExpireAtBefore(reservationStatusByBooked, localDateTime);
    }

    @Override
    public ReservationEntity addReservation(ReservationEntity reservationEntity) {
        return jpaReservationRepository.save(reservationEntity);
    }
}