package at.wreiner;

import at.wreiner.dto.ClassifyResponseDto;
import at.wreiner.dto.GenerationRequestDto;
import at.wreiner.entity.GenerationRequest;
import at.wreiner.entity.GenerationRequestStatus;
import at.wreiner.repository.GenerationRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Service
public class RestClient {

    private static final Logger log = LoggerFactory.getLogger(RestClient.class);

    @Autowired
    private GenerationRequestRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public boolean classifyGenerationRequest(UUID uuid) {
        log.info("Working on classification of UUID: {}", uuid);

        // Fetch and lock
        GenerationRequest existingGenerationRequest = repository.findByUuidLocked(uuid);
        if (existingGenerationRequest == null) {
            log.warn("Request with UUID {} not found", uuid);
            throw new RuntimeException("Request with UUID {} not found" + uuid);
        }

        return sendGenerationRequest(existingGenerationRequest);
    }

    private String buildJsonDataForRequest(GenerationRequest existingGenerationRequest) {
        GenerationRequestDto requestDto = new GenerationRequestDto(existingGenerationRequest.getPrompt());
        requestDto.setUuid(existingGenerationRequest.getUuid());
        return requestDto.toJSON();
    }

    private boolean sendGenerationRequest(GenerationRequest existingGenerationRequest) {
        String jsonData = buildJsonDataForRequest(existingGenerationRequest);
        log.debug("will send JSON data: {}", jsonData);

        // Prepare headers and HttpEntity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonData, headers);

        // Make the REST call
        String url = "http://192.168.2.102:8383/analyze";

        try {
            // Make the REST call and capture the response
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // Check if response is successful
            if (response.getStatusCode().is2xxSuccessful()) {
                ClassifyResponseDto responseDto = null;
                try {
                    responseDto = new ObjectMapper().readValue(response.getBody(), ClassifyResponseDto.class);
                } catch (JsonProcessingException e) {
                    log.warn("Error parsing response from classification service: {}", e.getMessage());
                    throw new RuntimeException("Error parsing response from classification service");
                }

                return processGenerationResponse(existingGenerationRequest, responseDto);
            } else {
                log.warn("Failed to get a successful response from classification service: {}", response.getStatusCode());
                throw new RuntimeException("Unsuccessful response from server");
            }
        } catch (Exception e) {
            throw new RuntimeException("error in sending classify request: {}" + e.getMessage());
        }
    }

    private boolean processGenerationResponse(GenerationRequest existingGenerationRequest, ClassifyResponseDto responseDto) {
        boolean isNsfw = responseDto.getLabel().equals("nsfw");

        if (isNsfw) {
            log.info("the prompt is classified as nsfw");
            existingGenerationRequest.setStatus(GenerationRequestStatus.NSFW);
        } else {
            existingGenerationRequest.setStatus(GenerationRequestStatus.CLASSIFIED);
        }

        try {
            repository.save(existingGenerationRequest);
        } catch (Exception e) {
            log.warn("Error updating classify response for UUID {}: {}", existingGenerationRequest.getUuid(), e.getMessage());
            throw new RuntimeException("Error updating generation response: {}" + e.getMessage());
        }

        return isNsfw;
    }
}

