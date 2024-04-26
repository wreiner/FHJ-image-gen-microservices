package at.wreiner;

import at.wreiner.dto.GenerationResponseDto;
import at.wreiner.entity.GenerationRequest;
import at.wreiner.entity.GenerationRequestStatus;
import at.wreiner.repository.GenerationRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttReceiver {

    private static final Logger log = LoggerFactory.getLogger(MqttReceiver.class);
    @Autowired
    private GenerationRequestRepository generationRequestRepository;

    @Autowired
    private GenerationRequestService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void receiveIngressMessage(String message) {
        log.info("Received ingress message: {}", message);

        GenerationRequest generationRequest = null;
        try {
            generationRequest = objectMapper.readValue(message, GenerationRequest.class);
            generationRequest.setStatus(GenerationRequestStatus.NEW);
            generationRequestRepository.save(generationRequest);
        } catch (Exception e) {
            System.err.println("error processing message: " + e.getMessage());
        }
        log.info("Saved generation request to database");

        if (generationRequest == null) {
            log.warn("Error parsing message");
            return;
        }
        log.info("Will update generation request to CLASSIFY for uuid: {}",
                generationRequest.getUuid());
        service.updateRequestStatus(generationRequest.getUuid(), GenerationRequestStatus.CLASSIFY);
        log.info("Updated generation request status in the database");
    }

    public void receiveGenerationResponseMessage(String message) {
        log.info("Received generation response: {}", message);

        GenerationResponseDto messageDTO = null;
        try {
            messageDTO = new ObjectMapper().readValue(message, GenerationResponseDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Error parsing message: {}", e.getMessage());
            return;
        }

        service.updateRequestStatus(messageDTO.getUuid(), messageDTO.getStatus());
        log.info("Updated generation request status in the database");
    }

}
