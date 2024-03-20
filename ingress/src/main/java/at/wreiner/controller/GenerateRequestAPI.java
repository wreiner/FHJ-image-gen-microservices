package at.wreiner.controller;

import at.wreiner.dto.GenerateRequest;
import at.wreiner.service.GenerateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenerateRequestAPI {
    @Autowired
    private GenerateRequestService generateRequestService;

    @GetMapping("/")
    public String home() {
        return "ingress-service is working!";
    }

    @PostMapping(value = "/createGenerateRequest", consumes = "application/json", produces = "application/json")
    public GenerateRequest createPerson(@RequestBody GenerateRequest request) {
        return generateRequestService.saveUpdateGenerateRequest(request);
    }
}