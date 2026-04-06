package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public Doctor addDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Transactional
    public boolean deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) return false;
        // Delete associated appointments first to avoid FK constraint violation
        List<com.project.back_end.models.Appointment> appts = appointmentRepository.findByDoctorId(id);
        appointmentRepository.deleteAll(appts);
        doctorRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Doctor> searchDoctors(String name, String specialty) {
        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
            return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (name != null && !name.isEmpty()) {
            return doctorRepository.findByNameContainingIgnoreCase(name);
        } else if (specialty != null && !specialty.isEmpty()) {
            return doctorRepository.findBySpecialtyIgnoreCase(specialty);
        }
        return doctorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> findByNameAndPassword(String name, String password) {
        if (name == null || password == null) return Optional.empty();
        return doctorRepository.findAll().stream()
                .filter(d -> d.getName().equals(name) && d.getPassword().equals(password))
                .findFirst();
    }
}
