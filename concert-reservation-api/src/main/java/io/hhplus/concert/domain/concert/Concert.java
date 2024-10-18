package io.hhplus.concert.domain.concert;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concert {

    private Long concertId;

    private String concertTitle;

    private Long concertPrice;

}