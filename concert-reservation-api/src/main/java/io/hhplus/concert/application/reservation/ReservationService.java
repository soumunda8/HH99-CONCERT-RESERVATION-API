package io.hhplus.concert.application.reservation;

import io.hhplus.concert.domain.repository.reservation.ReservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ModelMapper modelMapper;
    private final ReservationRepository reservationRepository;

    public ReservationService(ModelMapper modelMapper, ReservationRepository reservationRepository) {
        this.modelMapper = modelMapper;
        this.reservationRepository = reservationRepository;
    }

}