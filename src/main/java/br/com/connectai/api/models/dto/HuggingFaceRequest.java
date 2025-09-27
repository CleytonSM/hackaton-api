package br.com.connectai.api.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class HuggingFaceRequest {
    @JsonProperty("inputs")
    private String inputs;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    public HuggingFaceRequest() {}

    public HuggingFaceRequest(String inputs, Map<String, Object> parameters) {
        this.inputs = inputs;
        this.parameters = parameters;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
