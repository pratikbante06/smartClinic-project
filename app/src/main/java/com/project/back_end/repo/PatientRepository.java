package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByEmailAndPassword(String email, String password);

    boolean existsByEmailOrPhone(String email, String phone);

    List<Patient> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Patient p WHERE p.email = :email OR p.phone = :phone")
    List<Patient> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);
}