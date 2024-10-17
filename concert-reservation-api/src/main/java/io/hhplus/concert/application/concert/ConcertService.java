package io.hhplus.concert.application.concert;

import io.hhplus.concert.domain.repository.concert.ConcertRepository;
import io.hhplus.concert.domain.repository.concert.ConcertScheduleRepository;
import io.hhplus.concert.domain.repository.concert.SeatRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ConcertService {

    private final ModelMapper modelMapper;
    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    public ConcertService(ModelMapper modelMapper, ConcertRepository concertRepository, ConcertScheduleRepository concertScheduleRepository, SeatRepository seatRepository) {
        this.modelMapper = modelMapper;
        this.concertRepository = concertRepository;
        this.concertScheduleRepository = concertScheduleRepository;
        this.seatRepository = seatRepository;
    }

}