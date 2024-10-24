package io.hhplus.concert.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsResponse {

    private String userId;
    private String actionType;
    private int amount;
    private Long currentPoints;

}