package at.wreiner.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "generation_request")
public class GenerationRequest {
    @Id
    private UUID uuid;

    private String status;

    private String prompt;

    public GenerationRequest() {}

    public GenerationRequest(UUID uuid, String status, String prompt) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
