package com.example.limit_enforce_service.controller;


import com.example.limit_enforce_service.dto.ClientLicenseDto;
import com.example.limit_enforce_service.service.ClientLicenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client-licenses")
public class ClientLicenseController {

    private final ClientLicenseService clientLicenseService;

    public ClientLicenseController(ClientLicenseService clientLicenseService) {
        this.clientLicenseService = clientLicenseService;
    }

    // 🟢 Create or Update Client License
    @PostMapping
    public ResponseEntity<ClientLicenseDto> createOrUpdate(@RequestBody ClientLicenseDto clientLicenseDto) {
        ClientLicenseDto savedDto = clientLicenseService.saveOrUpdate(clientLicenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    // 🔵 Get All Client Licenses
    @GetMapping
    public ResponseEntity<List<ClientLicenseDto>> getAll() {
        List<ClientLicenseDto> clients = clientLicenseService.getAll();
        return ResponseEntity.ok(clients);
    }

    // 🔵 Get Client License by ID
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientLicenseDto> getById(@PathVariable String clientId) {
        ClientLicenseDto clientLicenseDto = clientLicenseService.getById(clientId);
        if (clientLicenseDto != null) {
            return ResponseEntity.ok(clientLicenseDto);
        }
        return ResponseEntity.notFound().build();
    }

    // 🔴 Delete Client License by ID
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteById(@PathVariable String clientId) {
        clientLicenseService.deleteById(clientId);
        return ResponseEntity.noContent().build();
    }

    // 🟡 Update Client License (Partial Update)
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientLicenseDto> update(
            @PathVariable String clientId,
            @RequestBody ClientLicenseDto clientLicenseDto) {
        // Ensure the clientId in path matches the DTO
        if (!clientId.equals(clientLicenseDto.getClientId())) {
            return ResponseEntity.badRequest().build();
        }

        ClientLicenseDto updatedDto = clientLicenseService.saveOrUpdate(clientLicenseDto);
        return ResponseEntity.ok(updatedDto);
    }
}
