package io.hhplus.concert.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSeatsResponse {

    private Long concertScheduleId;
    private Long remainingSeats;
    private List<Long> reservedSeatNumbers;

}