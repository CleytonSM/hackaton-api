package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.enums.SpecialtiesEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByEmail(String email);

    List<Doctor> findBySpecialty(String specialties);
}
