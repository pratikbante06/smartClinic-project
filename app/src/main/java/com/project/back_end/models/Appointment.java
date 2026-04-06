package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnore
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("appointmentDate")
    private LocalDate appointmentDate;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("appointmentTime")
    private LocalTime appointmentTime;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("status")
    private int status = 0;

    // Flat JSON fields to match expected API response shape
    @JsonProperty("doctorId")
    public Long getDoctorId() {
        return doctor != null ? doctor.getId() : null;
    }

    @JsonProperty("doctorName")
    public String getDoctorName() {
        return doctor != null ? doctor.getName() : null;
    }

    @JsonProperty("patientId")
    public Long getPatientId() {
        return patient != null ? patient.getId() : null;
    }

    @JsonProperty("patientName")
    public String getPatientName() {
        return patient != null ? patient.getName() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}