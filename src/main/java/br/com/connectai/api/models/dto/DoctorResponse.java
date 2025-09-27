package br.com.connectai.api.models.dto;

import br.com.connectai.api.models.db.Available;
import br.com.connectai.api.models.db.Doctor;

import java.util.List;

public class DoctorResponse {
    private Doctor doctor;
    private List<Available> availabilities;

    public DoctorResponse() {
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public List<Available> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<Available> availabilities) {
        this.availabilities = availabilities;
    }
}
