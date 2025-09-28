package br.com.connectai.api.models.dto;

import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;

import java.sql.Date;

public class ConsultSummaryDTO {
    private int id;
    private Patient patient;
    private DoctorDTO doctor;
    private Integer month;
    private String description;
    private Boolean hasHappened;
    private String consultDate;
    private String hour;
    private Date createdAt;

    public ConsultSummaryDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHasHappened() {
        return hasHappened;
    }

    public void setHasHappened(Boolean hasHappened) {
        this.hasHappened = hasHappened;
    }

    public String getConsultDate() {
        return consultDate;
    }

    public void setConsultDate(String consultDate) {
        this.consultDate = consultDate;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
