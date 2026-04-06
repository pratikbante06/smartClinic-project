package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("patientName")
    private String patientName;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("patientId")
    private Long patientId;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("doctorName")
    private String doctorName;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("appointmentId")
    private Long appointmentId;

    @ElementCollection
    @CollectionTable(name = "prescription_medications",
        joinColumns = @JoinColumn(name = "prescription_id"))
    @Column(name = "medication")
    @JsonProperty("medication")
    private List<String> medication;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("notes")
    private String notes;

    @Column
    @JsonProperty("createdAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public List<String> getMedication() { return medication; }
    public void setMedication(List<String> medication) { this.medication = medication; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
