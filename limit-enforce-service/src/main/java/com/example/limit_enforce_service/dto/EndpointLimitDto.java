package com.example.limit_enforce_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointLimitDto {
    private int perSecond;
    private int perMinute;
    private int perHour;
    private int perDay;
}