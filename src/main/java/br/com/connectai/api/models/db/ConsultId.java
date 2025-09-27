package br.com.connectai.api.models.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ConsultId implements Serializable {
    @Column(name = "id_patient")
    private Integer patientId;

    @Column(name = "id_doctor")
    private Integer doctorId;

    public ConsultId() {}

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
}
