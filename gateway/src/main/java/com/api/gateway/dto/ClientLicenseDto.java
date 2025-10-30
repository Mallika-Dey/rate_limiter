package com.api.gateway.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientLicenseDto {
    private String clientId;
    private String clientSecret;
    private Boolean active;
    private String clientExpiresAt;
    private Features features;
}
