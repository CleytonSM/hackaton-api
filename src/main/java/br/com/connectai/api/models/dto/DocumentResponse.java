package br.com.connectai.api.models.dto;

import br.com.connectai.api.models.db.Document;

import java.util.List;

public class DocumentResponse {
    private int patientId;
    private String patientName;
    private List<Document> documents;

    public DocumentResponse() {
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
