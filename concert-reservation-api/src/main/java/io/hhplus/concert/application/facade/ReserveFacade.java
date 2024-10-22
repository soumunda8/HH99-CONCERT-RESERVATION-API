package io.hhplus.concert.application.facade;

import io.hhplus.concert.application.concert.ConcertService;
import io.hhplus.concert.application.reservation.ReservationService;
import io.hhplus.concert.application.user.UserService;
import io.hhplus.concert.domain.concert.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveFacade {

    private final UserService userService;
    private final ConcertService concertService;
    private final ReservationService reservationService;

    @Autowired
    public ReserveFacade(UserService userService, ConcertService concertService, ReservationService reservationService) {
        this.userService = userService;
        this.concertService = concertService;
        this.reservationService = reservationService;
    }

    @Transactional
    public Long reserveSeat(Long seatNumber, Long concertScheduleId, String userId) {
        userService.checkUserStatus(userId);
        Seat seat = concertService.addSeatStatus(seatNumber, concertScheduleId, userId);
        return seat.getSeatId();
    }

}