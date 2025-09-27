package br.com.connectai.api.models.db;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_consults")
public class Consult {
    @EmbeddedId
    private ConsultId id;

    @ManyToOne
    @MapsId("patientId")
    @JoinColumn(name = "id_patient")
    private Patient patient;

    @ManyToOne
    @MapsId("doctorId")
    @JoinColumn(name = "id_doctor")
    private Doctor doctor;

    private Integer month;

    @Column(length = 255)
    private String description;

    @Column(name = "datetime_available")
    private Date consultDate;

    private Time hour;

    @Column(name = "created_at")
    private Date createdAt;

    public Consult() {}

}
