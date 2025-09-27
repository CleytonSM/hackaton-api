package br.com.connectai.api.models.dto;

public class ChatResponse {

    private String response;
    private String conversationId;
    private double confidence;
    private long responseTime;

    public ChatResponse() {}

    public ChatResponse(String response, String conversationId, double confidence, long responseTime) {
        this.response = response;
        this.conversationId = conversationId;
        this.confidence = confidence;
        this.responseTime = responseTime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
