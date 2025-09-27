package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query("SELECT p FROM Patient p WHERE p.email = ?1")
    Optional<Patient> findByEmail(String email);
}
