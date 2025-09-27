package br.com.connectai.api.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuggingFaceResponse {

    @JsonProperty("generated_text")
    private String generatedText;

    @JsonProperty("score")
    private Double score;

    public HuggingFaceResponse() {}

    public String getGeneratedText() {
        return generatedText;
    }

    public void setGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

