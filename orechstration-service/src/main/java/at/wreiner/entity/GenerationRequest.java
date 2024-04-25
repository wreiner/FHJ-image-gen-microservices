package at.wreiner.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "generation_request")
public class GenerationRequest {
    @Id
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private GenerationRequestStatus status;

    private String prompt;

    public GenerationRequest() {}

    public GenerationRequest(UUID uuid, GenerationRequestStatus status, String prompt) {
        this.uuid = uuid;
        this.status = status;
        this.prompt = prompt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public GenerationRequestStatus getStatus() {
        return status;
    }

    public void setStatus(GenerationRequestStatus status) {
        this.status = status;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
