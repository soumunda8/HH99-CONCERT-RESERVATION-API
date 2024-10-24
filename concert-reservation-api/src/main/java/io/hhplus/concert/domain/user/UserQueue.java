package io.hhplus.concert.domain.user;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueue {

    private Long queueId;

    private String userId;

    private QueueStatus queueStatus;

    private LocalDateTime createAt;

    private LocalDateTime queueExpireAt;

}