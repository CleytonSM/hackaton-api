package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.Available;
import br.com.connectai.api.models.db.Consult;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultRepository extends JpaRepository<Consult, Integer> {
    List<Consult> findAllByDoctor(Doctor doctor);

    List<Consult> findAllByPatient(Patient patient);
}
