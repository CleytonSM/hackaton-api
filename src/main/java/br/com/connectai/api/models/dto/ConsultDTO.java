package br.com.connectai.api.models.dto;

public class ConsultDTO {
    private int patientId;
    private int availableId;

    public ConsultDTO() {
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getAvailableId() {
        return availableId;
    }

    public void setAvailableId(int availableId) {
        this.availableId = availableId;
    }
}
