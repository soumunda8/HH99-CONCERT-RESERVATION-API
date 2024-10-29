package io.hhplus.concert.infrastructure.mapper.reservation;

import io.hhplus.concert.domain.reservation.Reservation;
import io.hhplus.concert.domain.reservation.ReservationStatus;
import io.hhplus.concert.infrastructure.entity.reservation.ReservationEntity;

public class ReservationMapper {

    public static Reservation toDomain(ReservationEntity entity) {
        return new Reservation(
                entity.getReservationId(),
                entity.getUserId(),
                entity.getSeatId(),
                ReservationStatus.valueOf(entity.getReservationStatus()),
                entity.getCreateAt(),
                entity.getReservationExpireAt()
        );
    }

    public static ReservationEntity toEntity(Reservation reservation) {
        return ReservationEntity.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .seatId(reservation.getSeatId())
                .reservationStatus(reservation.getReservationStatus().name())
                .reservationExpireAt(reservation.getReservationExpireAt())
                .build();
    }

}