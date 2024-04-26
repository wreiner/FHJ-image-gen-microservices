package at.wreiner.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassifyResponseDto {
    private String label;
    private float score;

    public ClassifyResponseDto() {
        // Jackson needs this
    }

    public ClassifyResponseDto(String label, float score) {
        this.label = label;
        this.score = score;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
