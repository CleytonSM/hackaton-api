package br.com.connectai.api.models.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_available")
public class Available {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_doctor")
    private Doctor doctor;

    @Column(name = "month")
    private Integer month;

    @Column(name = "time")
    private String time;

    @Column(name = "datetime_available")
    private LocalDateTime datetimeAvailable;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors, getters, and setters
    public Available() {}

    // ... getters and setters
}
