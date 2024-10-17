package io.hhplus.concert.domain.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPointHistory {

    private Long userHistoryId;

    private String userId;

    private PointActionType actionType;

    private Long changedPoint;

}
