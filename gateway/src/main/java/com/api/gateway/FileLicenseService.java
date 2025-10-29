package com.api.gateway;

import org.springframework.stereotype.Service;

import com.api.gateway.dto.AllowedEndpoint;
import com.api.gateway.dto.ClientLicenseDto;
import com.api.gateway.dto.Features;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileLicenseService {
    private static final Logger log = LoggerFactory.getLogger(FileLicenseService.class);

    //Thread-safe map for in-memory license storage
    private final Map<String, ClientLicenseDto> licenses = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final LettuceBasedProxyManager<byte[]> proxyManager;

    public FileLicenseService(LettuceBasedProxyManager<byte[]> proxyManager) {
        this.proxyManager = proxyManager;
    }

    public void updateLicense(ClientLicenseDto newLicense) {
        if (newLicense == null || newLicense.getClientId() == null) return;

        String clientId = newLicense.getClientId();

        // remove previous buckets in Redis (non blocking — SCAN + DEL)
        removeClientBuckets(clientId);

        // replace license in memory
        licenses.put(clientId, newLicense);
        log.info("Updated license and removed buckets for clientId={}", clientId);
    }

    /**
     * Remove all Bucket4j buckets for a client.
     * This is the correct way in production when using LettuceBasedProxyManager.
     */
    private void removeClientBuckets(String clientId) {
        if (clientId == null || clientId.isEmpty()) return;

        try {
            // Get all allowed endpoints for this client
            List<AllowedEndpoint> endpoints = getAllowedEndpoints(clientId);
            int totalDeleted = 0;

            for (AllowedEndpoint endpoint : endpoints) {
                String endpointPath = endpoint.getPath();
                String bucketKey = clientId + ":" + endpointPath;

                // ✅ Use removeProxy instead of non-existing remove
                proxyManager.removeProxy(bucketKey.getBytes());
                totalDeleted++;

                log.debug("🧹 Deleted bucket for clientId={}, endpoint={}", clientId, endpointPath);
            }

            log.info("✅ Finished deleting {} buckets for clientId={}", totalDeleted, clientId);

        } catch (Exception e) {
            log.warn("⚠️ Failed to remove buckets for clientId={} : {}", clientId, e.getMessage());
        }
    }

    public List<AllowedEndpoint> getAllowedEndpoints(String clientId) {
        ClientLicenseDto license = licenses.get(clientId);
        return license != null && license.getFeatures() != null ?
                license.getFeatures().getAllowedEndpoints() : new ArrayList<>();
    }

    public boolean licenseExists(String clientId) {
        return licenses.containsKey(clientId);
    }

    public Map<String, ClientLicenseDto> getAllClients() {
        return new HashMap<>(licenses);
    }

    public Boolean isActive(String clientId) {
        ClientLicenseDto license = licenses.get(clientId);
        return license != null ? license.getActive() : null;
    }

    public String getClientExpiresAt(String clientId) {
        ClientLicenseDto license = licenses.get(clientId);
        return license != null ? license.getClientExpiresAt() : null;
    }

    public List<String> getBlockedEndpoints(String clientId) {
        ClientLicenseDto license = licenses.get(clientId);
        return license != null && license.getFeatures() != null ?
                license.getFeatures().getBlockedEndpoints() : new ArrayList<>();
    }

    public boolean isEndpointBlocked(String clientId, String requestPath) {
        List<String> blockedEndpoints = getBlockedEndpoints(clientId);
        return blockedEndpoints.stream()
                .anyMatch(blockedPattern -> matchesPathPattern(requestPath, blockedPattern));
    }

    /**
     * ✅ Helper method to check if a request path matches a pattern
     * Maintains compatibility with existing RateLimitFilter
     */
    public boolean matchesPathPattern(String requestPath, String pattern) {
        // Convert pattern to regex - using same logic as RateLimitFilter
        String regex = pattern
                .replace(".", "\\.") // escape dots
                .replace("**", ".*") // ** matches any characters including /
                .replace("*", ".*") // * matches any characters (even across /)
                .replace("?", "."); // ? matches any single character
        return requestPath.matches(regex);
    }
}
