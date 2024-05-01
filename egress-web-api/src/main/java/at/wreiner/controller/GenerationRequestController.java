package at.wreiner.controller;

import at.wreiner.entity.GenerationRequest;
import at.wreiner.repository.GenerationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/egress")
public class GenerationRequestController {
    @Autowired
    private GenerationRequestRepository repository;

    @GetMapping("/status/{uuid}")
    public ResponseEntity<GenerationRequest> getGenerationRequest(@PathVariable UUID uuid) {
        Optional<GenerationRequest> generationRequest = repository.findByUuid(uuid);
        return generationRequest.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
