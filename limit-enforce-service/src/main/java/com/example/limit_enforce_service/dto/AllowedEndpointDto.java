package com.example.limit_enforce_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllowedEndpointDto {
    private String path;
    private String endPointExpiresAt;
    private EndpointLimitDto limits;
}