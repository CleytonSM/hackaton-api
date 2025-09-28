package br.com.connectai.api.models.dto;

import br.com.connectai.api.models.db.Available;
import br.com.connectai.api.models.db.Doctor;

import java.util.List;

public class DoctorResponse {
    private DoctorDTO doctor;
    private List<AvailableDTO> availabilities;

    public DoctorResponse() {
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public List<AvailableDTO> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<AvailableDTO> availabilities) {
        this.availabilities = availabilities;
    }
}
