package at.wreiner.dto;

import at.wreiner.entity.GenerationRequestStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerationResponseDto {
    private UUID uuid;
    private GenerationRequestStatus status;
    private String errorString;

    public GenerationResponseDto() {
        // Jackson needs this
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

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}
