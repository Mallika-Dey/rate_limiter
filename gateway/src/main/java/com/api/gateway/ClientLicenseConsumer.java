package com.api.gateway;

import com.api.gateway.dto.ClientLicenseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ClientLicenseConsumer {
    private static final Logger log = LoggerFactory.getLogger(ClientLicenseConsumer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileLicenseService fileLicenseService;

    public ClientLicenseConsumer(FileLicenseService fileLicenseService) {
        this.fileLicenseService = fileLicenseService;
    }

    /**
     * ✅ Consumes license update events from Kafka.
     * The producer (license service) sends one message per clientId.
     */
    @KafkaListener(topics = "${app.kafka.topic.client-license}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String messageJson) {
        log.info("📥 Raw JSON Message: {}", messageJson);

        try {
            ClientLicenseDto dto = objectMapper.readValue(messageJson, ClientLicenseDto.class);

            // Update the in-memory license map
            fileLicenseService.updateLicense(dto);

            System.out.println("✅ [Kafka] Updated in-memory license for clientId=" + dto.getClientId());
        } catch (Exception e) {
            log.error("❌ Failed to parse message", e);
        }
    }
}
