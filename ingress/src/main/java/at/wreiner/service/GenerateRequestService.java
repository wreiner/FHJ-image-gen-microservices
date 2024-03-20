package at.wreiner.service;

import at.wreiner.dto.GenerateRequest;

import java.util.UUID;

public interface GenerateRequestService {
    public GenerateRequest saveUpdateGenerateRequest(GenerateRequest request);
    public GenerateRequest getGenerateRequest(UUID uuid);
}
