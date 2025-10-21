package com.example.limit_enforce_service.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLicensesConfigDto {
    private Map<String, ClientLicenseDto> clients;
}