package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.Available;
import br.com.connectai.api.models.db.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableRepository extends JpaRepository<Available, Integer> {
    List<Available> findAllByDoctor(Doctor doctor);
}
