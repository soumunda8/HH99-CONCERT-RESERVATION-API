package io.hhplus.concert.domain.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String userId;

    private Long points = 0L;

}