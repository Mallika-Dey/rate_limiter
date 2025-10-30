package com.example.limit_enforce_service.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLicenseDto {
    private String clientId;
    private String clientSecret;
    private Boolean active;
    private String clientExpiresAt;
    private FeaturesDto features;
}
