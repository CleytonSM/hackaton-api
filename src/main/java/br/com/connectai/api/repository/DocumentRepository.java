package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Document;
import br.com.connectai.api.models.db.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByPatient(Patient patient);

    List<Document> findAllByDoctor(Doctor doctor);
}
